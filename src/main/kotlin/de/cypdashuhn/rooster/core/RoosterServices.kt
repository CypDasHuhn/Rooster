package de.cypdashuhn.rooster.core

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

interface RoosterService

object RoosterServices {

    private val services: MutableMap<KClass<out RoosterService>, RoosterService> = mutableMapOf()

    fun <T : RoosterService, S : T> set(clazz: KClass<T>, instance: S): S {
        require(clazz.supertypes.any { it.classifier == RoosterService::class }) {
            "Service class must directly extend RoosterService, but ${clazz.simpleName} does not."
        }
        services[clazz] = instance
        return instance
    }

    inline fun <reified T : RoosterService> set(instance: T) = set(T::class, instance)

    fun <T : RoosterService> get(clazz: KClass<T>): T {
        return services[clazz] as? T ?: error("Service ${clazz.simpleName} not found.")
    }

    inline fun <reified T : RoosterService> get(): T = get(T::class)

    fun <T : RoosterService> getIfPresent(clazz: KClass<T>): T? = services[clazz] as? T
    inline fun <reified T : RoosterService> getIfPresent(): T? = getIfPresent(T::class)

    fun <T : RoosterService> delegate(clazz: KClass<T>): Delegate<T> =
        ReadOnlyProperty { _, _ -> get(clazz) }

    inline fun <reified T : RoosterService> delegate(): Delegate<T> = delegate(T::class)

    fun <T : RoosterService, S : T> setDelegate(clazz: KClass<T>, instance: S): Delegate<T> {
        set(clazz, instance)
        return delegate(clazz)
    }

    inline fun <reified T : RoosterService, S : T> setDelegate(instance: S): Delegate<T> =
        setDelegate(T::class, instance)
}

typealias Delegate<T> = ReadOnlyProperty<Any?, T>