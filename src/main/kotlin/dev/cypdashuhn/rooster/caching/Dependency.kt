package dev.cypdashuhn.rooster.caching

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import org.bukkit.entity.Player

infix fun <T : Context> (T.() -> Array<Any>).and(type: DependencyType): InterfaceDependency<T> {
    return InterfaceDependency.default<T>().dependsOnContext(this) and type
}

enum class DependencyType {
    PLAYER,
    SLOT;

    infix fun and(type: DependencyType): InterfaceDependency<Context> {
        return InterfaceDependency.default<Context>() and this and type
    }

    infix fun <T : Context> and(contextDependencySelector: T.() -> Array<Any>): InterfaceDependency<T> {
        return InterfaceDependency.default<T>() and (this) and contextDependencySelector
    }
}

class DependencyKey<T, E>(
    val key: String,
    val informationSelector: (T) -> E
)

abstract class Dependency<E> {
    protected val dependencyMap = mutableMapOf<String, (E) -> Array<Any>>()
    protected val dependencies by lazy { dependencyMap.map { it.value } }
    private val dependencyKeys: List<DependencyKey<E, *>>

    protected fun <K> key(key: String, infoSelector: (E) -> K): DependencyKey<E, K> =
        DependencyKey(key, infoSelector)

    protected fun <T> set(key: DependencyKey<E, T>, value: Pair<Boolean, (T.() -> Array<Any>)?>) {
        val (enabled, value) = value
        if (enabled) {
            dependencyMap[key.key] = {
                val selector: (T.() -> Array<Any>) = value ?: { arrayOf(this as Any) }
                key.informationSelector(it).selector()
            }
        } else dependencyMap.remove(key.key)
    }

    fun <T> get(key: DependencyKey<E, T>) = dependencyMap[key.key]

    abstract fun dependencyKeys(): List<DependencyKey<E, *>>

    init {
        dependencyKeys = dependencyKeys()
    }

    val dependsOnNothing
        get() = dependencyMap.isEmpty()
    val dependsOnEverything
        get() = dependencyKeys.size == dependencyMap.size

    private fun setMap(dependencyMap: MutableMap<String, (E) -> Array<Any>>) {
        this.dependencyMap.putAll(dependencyMap)
    }

    protected inline fun <reified T : Dependency<E>> copySelf(
        block: T.() -> Unit
    ): T {
        val instance: T = T::class.constructors.first { it.parameters.isEmpty() }.call()
        instance.dependencyMap.putAll(dependencyMap)
        block(instance)
        return instance
    }

    var createKey: (Int, E) -> CacheKey = { idx, e ->
        val plans = getPlan(dependencies.map { it(e) }.flatten(idx))
        createKey = { idx, e ->
            CacheKey(pack(dependencies.map { it(e) }.flatten(idx), plans))
        }
        createKey(idx, e)
    }
}

fun List<Array<Any>>.flatten(lambdaKey: Int): Array<Any> {
    val totalSize = sumOf { it.size }
    val result = arrayOfNulls<Any>(totalSize + 1)
    var index = 0
    for (array in this) {
        for (element in array) {
            result[index++] = element
        }
    }
    result[index] = lambdaKey
    @Suppress("UNCHECKED_CAST")
    return result as Array<Any>
}


class InterfaceDependency<T : Context> : Dependency<InterfaceInfo<T>> {
    var playerKey = key("player") { it.player }
    val slotKey = key("slot") { it.slot }
    val contextKey = key("context") { it.context }

    override fun dependencyKeys(): List<DependencyKey<InterfaceInfo<T>, *>> {
        return listOf(playerKey, slotKey, contextKey)
    }

    companion object {
        fun <T : Context> none() = InterfaceDependency<T>()

        fun <T : Context> default() = none<T>()

        fun <T : Context> all() = default<T>()
            .dependsOnPlayer()
            .dependsOnContext()
            .dependsOnContext()

        fun <T : Context> dependsOnPlayer(selector: (Player.() -> Array<Any>)? = null) =
            default<T>().dependsOnPlayer(selector)

        fun <T : Context> dependsOnSlot(selector: (Int.() -> Array<Any>)? = null) = default<T>().dependsOnSlot(selector)
        fun <T : Context> dependsOnContext(selector: (T.() -> Array<Any>)? = null) =
            default<T>().dependsOnContext(selector)
    }

    private constructor()

    fun dependsOnPlayer(selector: (Player.() -> Array<Any>)? = null) = copy { this.set(playerKey, true to selector) }
    fun dependsOnSlot(selector: (Int.() -> Array<Any>)? = null) = copy { this.set(slotKey, true to selector) }
    fun dependsOnContext(selector: (T.() -> Array<Any>)? = null) = copy {
        if (selector == null) this.set(contextKey, true to null)
        else this.set(contextKey, true to selector)
    }

    fun doesntDependOnContext() = copy {
        this.set(contextKey, false to null)
    }

    fun copy(block: InterfaceDependency<T>.() -> Unit) = copySelf(block)

    infix fun and(type: DependencyType): InterfaceDependency<T> {
        return when (type) {
            DependencyType.SLOT -> dependsOnSlot()
            DependencyType.PLAYER -> dependsOnPlayer()
        }
    }

    infix fun and(contextDependencySelector: T.() -> Array<Any>): InterfaceDependency<T> {
        return dependsOnContext(contextDependencySelector)
    }
}