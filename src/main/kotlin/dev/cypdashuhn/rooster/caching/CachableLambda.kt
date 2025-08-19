package dev.cypdashuhn.rooster.caching

import com.google.common.cache.CacheBuilder
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo

class CacheKey(
    val key: LongArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CacheKey) return false
        return key.contentEquals(other.key)
    }

    override fun hashCode(): Int {
        return key.contentHashCode()
    }
}

class InterfaceChachableLambda<T : Context, E> {
    companion object {
        private var highestLambdaIndex = 0
        fun getNextLambdaIndex() = highestLambdaIndex++
        val cache = CacheBuilder.newBuilder().build<CacheKey, Any>()
    }

    private val lambda: InterfaceInfo<T>.() -> E
    private val lambdaKey = getNextLambdaIndex()
    val dependency: InterfaceDependency<T>

    var get: InterfaceInfo<T>.() -> E

    constructor(
        lambda: InterfaceInfo<T>.() -> E,
        dependency: InterfaceDependency<T>
    ) {
        this.lambda = lambda
        this.dependency = dependency
        get = lambda

        if (dependency.dependsOnNothing) {
            get = { info: InterfaceInfo<T> ->
                var res = lambda(info)
                get = { res }
                res
            }
        } else if (!dependency.dependsOnEverything) {
            get = { info: InterfaceInfo<T> ->
                val key = dependency.createKey(lambdaKey, info)
                cache.get(key) { lambda(info) } as E
            }
        }
    }

    constructor(
        value: E
    ) {
        lambda = { value }
        dependency = InterfaceDependency.none<T>()
        get = { value }
    }
}