package de.cypdashuhn.rooster.ui

import de.cypdashuhn.rooster.Rooster.cache
import de.cypdashuhn.rooster.Rooster.interfaceContextProvider
import de.cypdashuhn.rooster.Rooster.registeredInterfaces
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import kotlin.reflect.full.safeCast

typealias InterfaceName = String

object InterfaceManager {
    /**
     * A map which links players to an interface, and whether its
     * state is currently protected (as to not be overwritten by the
     * InventoryOpeningListener, which would else set it as empty.
     * This needs to be performed while changing between interfaces)
     */
    var playerInterfaceMap = HashMap<Player, InterfaceName>()
    fun setPlayerEmpty(player: Player) {
        playerInterfaceMap[player] = ""
    }

    const val CHANGES_INTERFACE_KEY = "changes_interface"

    /**
     * This function opens the interface it could find depending on the
     * [targetInterface] for the given [player] applied with the current state
     * of the interface ([context]).
     */
    fun <T : Context> openTargetInterface(player: Player, targetInterface: Interface<T>, context: T) {
        cache.set(CHANGES_INTERFACE_KEY, player, true)

        playerInterfaceMap[player] = targetInterface.interfaceName

        val inventory =
            targetInterface.getInventory(player, context)
                .fillInventory(targetInterface.items, context, player)

        interfaceContextProvider.updateContext(player, targetInterface, context)

        player.openInventory(inventory)
    }

    /**
     * This function fills the [Inventory] with [ItemStack]'s using the
     * registered [clickableItems]'s and the current Interface State
     * ([context]).
     */
    private fun <T : Context> Inventory.fillInventory(
        clickableItems: List<InterfaceItem<T>>,
        context: T,
        player: Player
    ): Inventory {
        for (slot in 0 until this.size) {
            clickableItems
                .filter { it.condition(InterfaceInfo(slot, context, player)) }
                .forEach { this@fillInventory.setItem(slot, it.itemStackCreator(InterfaceInfo(slot, context, player))) }
        }
        return this
    }

    fun <T : Context> getItems(
        slotAmount: Int,
        items: List<InterfaceItem<T>>,
        context: T,
        player: Player
    ): Map<Slot, InterfaceItem<T>> {
        val map = mutableMapOf<Slot, InterfaceItem<T>>()
        for (slot in 0 until slotAmount) {
            items.filter { it.condition(InterfaceInfo(slot, context, player)) }
                .forEach { map[slot] = it }
        }
        return map
    }

    fun handleInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        val interfaceName = playerInterfaceMap[player] ?: return
        if (interfaceName.isEmpty()) return

        val correspondingInterface = registeredInterfaces
            .firstOrNull { currentInterface -> currentInterface.interfaceName == interfaceName }
            ?: return

        if (event.currentItem == null && correspondingInterface.ignoreEmptySlots) return
        if (event.inventory is PlayerInventory && correspondingInterface.ignorePlayerInventory) return

        val click = Click(event, player, event.currentItem, event.currentItem?.type, event.slot)

        @Suppress("UNCHECKED_CAST")
        val typedInterface = correspondingInterface as Interface<Context>

        val context = typedInterface.getContext(player)

        val typedContext = typedInterface.contextClass.safeCast(context)

        if (typedContext != null) {
            event.isCancelled =
                typedInterface.cancelEvent(ClickInfo(click, typedContext, event, correspondingInterface))

            typedInterface.items
                .filter { it.condition(InterfaceInfo(click.slot, typedContext, event.whoClicked as Player)) }
                .forEach { it.action(ClickInfo(click, typedContext, event, correspondingInterface)) }
        } else {
            println("Failed to cast context to the expected type ${typedInterface.contextClass}")
        }
    }
}