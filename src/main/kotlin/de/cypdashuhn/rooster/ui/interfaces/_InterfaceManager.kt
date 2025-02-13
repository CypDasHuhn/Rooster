package de.cypdashuhn.rooster.ui.interfaces

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object _InterfaceManager {
    fun <T : Context> click(
        click: Click,
        inventoryClickEvent: InventoryClickEvent,
        targetInterface: Interface<T>,
        player: Player
    ) {
        val context = targetInterface.getContext(player)
        val map = targetInterface.groupedItems(player, context)

        val interfaceInfo = InterfaceInfo(click.slot, context, player)

        map[click.slot]
            ?.filter { it.totalCondition(interfaceInfo) }
            ?.sortedBy { it.priority(interfaceInfo) }
            ?.forEach { it.action(ClickInfo(click, context, inventoryClickEvent, targetInterface)) }
    }

    fun <T : Context> getInventory(
        targetInventory: Interface<T>,
        context: T,
        player: Player
    ): Inventory {
        val inventory = targetInventory.getInventory(player, context)
        val map = targetInventory.groupedItems(player, context)

        for (slot in 0 until inventory.size) {
            val items = map[slot] ?: continue

            val itemStack = items
                .filter { it.totalCondition(InterfaceInfo(slot, context, player)) }
                .minByOrNull { it.priority(InterfaceInfo(slot, context, player)) }
                ?.let {
                    val item = it.itemStackCreator(InterfaceInfo(slot, context, player))
                    inventory.setItem(slot, item)
                }
        }

        return inventory
    }

    internal inline fun <reified T : Context> updateInventory(
        event: InventoryClickEvent,
        player: Player,
        targetInterface: Interface<T>,
        oldContext: T,
        context: T,
    ) {
        val inventory = targetInterface.getInventory(player, context)
        val map = targetInterface.groupedItems(player, context)

        val changedProperties = diffProperties(oldContext, context)
        for (slot in 0 until inventory.size) {
            val items = map[slot] ?: continue
            items
                .sortedBy { it.priority(InterfaceInfo(slot, context, player)) }
                .map { it to (it.dependsOn == null || it.dependsOn!!.any { it in changedProperties }) }
                .forEach {

                }

        }

        event.inventory.setItem()
    }

    inline fun <reified T : Any> diffProperties(instance1: T, instance2: T): List<KProperty1<T, *>> {
        return T::class.memberProperties.filter { property ->
            property.get(instance1) != property.get(instance2)
        }
    }

}