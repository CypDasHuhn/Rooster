package dev.cypdashuhn.rooster.caching

import sun.misc.Unsafe
import java.lang.reflect.Field
import kotlin.reflect.KProperty1

internal object UnsafeValues {
    private val theUnsafe: Unsafe
        get() {
            val f: Field = Unsafe::class.java.getDeclaredField("theUnsafe")
            f.isAccessible = true
            return f.get(null) as Unsafe
        }

    private val valueMap: MutableMap<Class<*>, Any> = mutableMapOf()
    fun <T> getValue(clazz: Class<T>): T =
        (valueMap[clazz] ?: theUnsafe.allocateInstance(clazz).also { valueMap[clazz] = it }) as T

    fun <T> getProperties(clazz: Class<T>, propertySelector: (T) -> List<KProperty1<T, *>>): List<KProperty1<T, *>> {
        val value = getValue(clazz)
        try {
            return propertySelector(value)
        } catch (e: NullPointerException) {
            throw RuntimeException("Please don't try to access values in the property selector.")
        }
    }
}