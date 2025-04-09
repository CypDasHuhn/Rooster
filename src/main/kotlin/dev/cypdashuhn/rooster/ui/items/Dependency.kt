package dev.cypdashuhn.rooster.ui.items

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

typealias PropertyListProvider<T> = T.() -> List<KProperty1<T, *>>

infix fun <T : Context> PropertyListProvider<T>.and(type: DependencyType): Dependency<T> {
    return Dependency.default<T>().dependsOnContext(this) and type
}

enum class DependencyType {
    PLAYER,
    SLOT;

    infix fun and(type: DependencyType): Dependency<Context> {
        return Dependency.default<Context>() and this and type
    }

    infix fun <T : Context> and(contextDependencySelector: PropertyListProvider<T>): Dependency<T> {
        return Dependency.default<T>() and (this) and contextDependencySelector
    }
}

class Dependency<T : Context> {
    var dependsOnPlayer: Boolean = false
        private set
    var dependsOnSlot: Boolean = false
        private set
    var dependsOnContext: Boolean = false
        private set
    var contextDependencySelector: PropertyListProvider<T>? = null
        private set

    fun getDependencyList(contextClass: KClass<T>): List<KProperty1<T, *>> {
        val obj = contextClass.constructors.first { it.parameters.isEmpty() }.call()
        return contextDependencySelector?.invoke(obj) ?: emptyList()
    }

    val dependsOnNothing
        get() = !dependsOnPlayer && !dependsOnSlot && !dependsOnContext
    val dependsOnEverything
        get() = dependsOnPlayer && dependsOnSlot && dependsOnContext && contextDependencySelector == null

    companion object {
        fun <T : Context> none() = Dependency<T>()

        fun <T : Context> default() = none<T>()

        fun <T : Context> all() = default<T>()
            .dependsOnPlayer()
            .dependsOnContext()
            .dependsOnContext()

        fun <T : Context> dependsOnPlayer(value: Boolean = true) = default<T>().dependsOnPlayer(value)
        fun <T : Context> dependsOnSlot(value: Boolean = true) = default<T>().dependsOnSlot(value)
        fun <T : Context> dependsOnContext(selector: PropertyListProvider<T>? = null) =
            default<T>().dependsOnContext(selector)
    }

    private constructor()

    fun dependsOnPlayer(value: Boolean = true): Dependency<T> = copy { it.dependsOnPlayer = value }
    fun dependsOnSlot(value: Boolean = true): Dependency<T> = copy { it.dependsOnSlot = value }
    fun dependsOnContext(selector: PropertyListProvider<T>? = null) = copy {
        it.dependsOnContext = true
        it.contextDependencySelector = selector
    }

    fun doesntDependOnContext() = copy {
        it.dependsOnContext = false
    }

    fun copy(block: (Dependency<T>) -> Unit) = Dependency<T>().also {
        it.dependsOnPlayer = dependsOnPlayer
        it.dependsOnSlot = dependsOnSlot
        it.dependsOnContext = dependsOnContext
        it.contextDependencySelector = contextDependencySelector
        block(it)
    }

    infix fun and(type: DependencyType): Dependency<T> {
        return when (type) {
            DependencyType.SLOT -> dependsOnSlot()
            DependencyType.PLAYER -> dependsOnPlayer()
        }
    }

    infix fun and(contextDependencySelector: PropertyListProvider<T>): Dependency<T> {
        return dependsOnContext(contextDependencySelector)
    }

    var createKey: (clazz: KClass<T>) -> ((InterfaceInfo<T>) -> Int) = { clazz ->
        val mapEntries = mutableListOf<(InterfaceInfo<T>) -> Int>()
        if (dependsOnPlayer) mapEntries += { it.player.hashCode() }
        if (dependsOnSlot) mapEntries += { it.slot }
        if (dependsOnContext) getDependencyList(clazz).forEach { dep ->
            mapEntries += { dep.get(it.context).hashCode() }
        }

        createKey = { clazz ->
            val keyCreator = { info: InterfaceInfo<T> ->
                mapEntries.map { it(info) }.hashCode()
            }
            keyCreator
        }

        val keyCreator = { info: InterfaceInfo<T> ->
            mapEntries.map { it(info) }.hashCode()
        }
        keyCreator
    }
}