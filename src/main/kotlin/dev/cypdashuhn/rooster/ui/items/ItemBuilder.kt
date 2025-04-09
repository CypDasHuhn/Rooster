package dev.cypdashuhn.rooster.ui.items

import com.google.common.cache.CacheBuilder
import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import dev.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.SortedSet
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

class ItemBuilder<T : Context> {
    constructor(clazz: KClass<T>) {
        this.contextClass = clazz
        this.condition = ConditionMap(clazz)
    }

    private val contextClass: KClass<T>

    private var slots: Array<Int>? = null
    private var priority = CachableLambda<T, Int>(0)
    private var condition: ConditionMap<T>
    private var items: CachableLambda<T, ItemStack>? = null

    private var action: ClickInfo<T>.() -> Unit = { }

    companion object {
        fun <T : Context> map(items: List<ItemBuilder<T>>) {

            for (slot in 0..(6 * 9)) {
                items.filter { it.slots == null || it.slots!!.contains(slot) }.withIndex()


            }
        }
    }

    fun usedWhen(
        conditionKey: String = ConditionMap.ANONYMOUS_KEY,
        dependency: Dependency<T> = Dependency.all<T>(),
        condition: InterfaceInfo<T>.() -> Boolean
    ) = copy {
        this.condition.set(condition, conditionKey, dependency)
    }

    fun atSlot(slot: Int) = copy {
        this.slots = arrayOf(slot)
    }

    fun atSlots(vararg slots: Int) = atSlots(slots.toList())

    fun atSlots(slots: List<Int>) = copy {
        this.slots = slots.toTypedArray()
    }

    fun forAllSlots() = copy {}

    fun resetConditions(excludingConditionKeys: List<String>) = copy {
        this.condition.resetConditions(excludingConditionKeys)
    }

    fun priority(
        priority: InterfaceInfo<T>.() -> Int,
        dependency: Dependency<T> = Dependency.all<T>()
    ): ItemBuilder<T> = copy {
        this.priority = CachableLambda(priority, dependency, contextClass)
    }

    fun priority(priority: Int): ItemBuilder<T> = copy {
        this.priority = CachableLambda(priority)
    }

    fun onClick(action: ClickInfo<T>.() -> Unit): ItemBuilder<T> = copy { this.action = action }

    fun displayAs(itemStackCreator: InterfaceInfo<T>.() -> ItemStack) =
        copy { this.items = itemStackCreator.toCachableLambda(contextClass) }

    fun displayAs(
        dependency: Dependency<T>,
        itemStackCreator: InterfaceInfo<T>.() -> ItemStack,
    ) =
        copy { this.items = itemStackCreator.toCachableLambda(contextClass, dependency) }

    fun displayAs(itemStack: ItemStack): ItemBuilder<T> = copy { this.items = CachableLambda(itemStack) }

    fun copy(modifyingBlock: ItemBuilder<T>.() -> Unit): ItemBuilder<T> {
        val copy = ItemBuilder<T>(contextClass).also {
            it.condition = condition.copy()
            it.items = items
            it.action = action
            it.priority = priority
        }
        copy.modifyingBlock()
        return copy
    }

    class ItemsForSlot<T : Context>(
        val items: List<ItemBuilder<T>>,
        val contextClass: Class<T>
    ) {
        var dynamicPriorityItems: List<ItemBuilder<T>> = listOf()
        var staticPriorityItems: List<ItemBuilder<T>> = listOf()
        var staticPriorityItemsSorted: SortedSet<ItemBuilder<T>>? = null

        init {
            val grouped = items.groupBy { it.priority.dependency.dependsOnNothing }
            staticPriorityItems = grouped[true] ?: emptyList()
            dynamicPriorityItems = (grouped[false] ?: emptyList())
        }

        var get: (InterfaceInfo<T>) -> ItemBuilder<T>? = { info ->
            val highestStaticItem = staticPriorityItems
                }

            newCombinedItems = dynamicPriorityItems.toMutableList()
            if (highestStaticItem != null) newCombinedItems!!.add(highestStaticItem)

            get = if (newCombinedItems!!.size <= 1) {
                { newCombinedItems!!.firstOrNull() }
            } else {
                { info ->
                    newCombinedItems!!.minBy { it.priority.get(info) }
                }
            }

            newCombinedItems!!.minBy { it.priority.get(info) }
        }
    }

    class CachableLambda<T : Context, E> {
        private val lambda: InterfaceInfo<T>.() -> E
        val dependency: Dependency<T>
        private var clazz: KClass<T>? = null

        constructor(
            lambda: InterfaceInfo<T>.() -> E,
            dependency: Dependency<T>,
            clazz: KClass<T>
        ) {
            this.lambda = lambda
            this.dependency = dependency
            this.clazz = clazz
            get = lambda
            init()
        }

        constructor(value: E) {
            lambda = { value }
            dependency = Dependency.none<T>()
            get = { value }
        }

        val cache = CacheBuilder.newBuilder().build<Int, E>()
        var get: (InterfaceInfo<T>) -> E

        fun init() {
            if (dependency.dependsOnNothing) {
                get = { info ->
                    var res = lambda(info)
                    get = { res }
                    res
                }
            } else if (!dependency.dependsOnEverything) {
                get = { info ->
                    val key = dependency.createKey(clazz!!)(info)
                    cache.get(key) { lambda(info) }
                }
            }
        }
    }
}

fun <T : Context, E> (InterfaceInfo<T>.() -> E).toCachableLambda(
    clazz: KClass<T>,
    dependency: Dependency<T> = Dependency.all<T>()
) = ItemBuilder.CachableLambda(this, dependency, clazz)

fun main() {

    ItemBuilder(NoContextInterface.EmptyContext::class)
        .atSlot(1)
        .displayAs { createItem(Material.COMPASS) }
        .onClick { }
}

fun s() {
    val info = InterfaceInfo<PageInterface.PageContext>()
    val e = info::class.
    val s = info::class.memberProperties.first { it.name == "prop" }
    s.get(info)
}
