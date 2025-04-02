package dev.cypdashuhn.rooster.ui.items

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemBuilder<T : Context> {
    private var condition: ConditionMap<T> = ConditionMap()
    private var items: ItemStackCreator<T>? = null
    private var action: (ClickInfo<T>) -> Unit = { }
    private var priority: ((InterfaceInfo<T>) -> Int)? = null

    fun build(): InterfaceItem<T> = InterfaceItem(condition = condition.flatten(), itemStackCreator = items!!.invoke(), action = action, priority = priority)

    fun usedWhen(condition: InterfaceInfo<T>.() -> Boolean): ItemBuilder<T> = apply {
        this.condition
    }

    fun priority(priority: InterfaceInfo<T>.() -> Int): ItemBuilder<T> = apply { this.priority = priority }

    fun action(action: (ClickInfo<T>) -> Unit): ItemBuilder<T> = apply { this.action = action }

    fun displayAs(itemStackCreator: ItemStackCreator<T>): ItemBuilder<T> = apply { this.items = itemStackCreator }
    fun displayAs(itemStack: ItemStack): ItemBuilder<T> = apply { this.items = ItemStackCreator(itemStack) }

    fun atSlot(slot: Int): ItemBuilder<T> = apply {  }
}

fun main() {
    val item = ItemBuilder<NoContextInterface.EmptyContext>()
        .usedWhen { player.isFlying }
        .priority { 0 }
        .displayAs(createItem(Material.COMPASS))
        .build()
}