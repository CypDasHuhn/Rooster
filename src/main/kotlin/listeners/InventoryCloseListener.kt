package de.cypdashuhn.rooster.listeners

import de.cypdashuhn.rooster.Rooster.cache
import de.cypdashuhn.rooster.ui.InterfaceManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent

@Suppress("unused")
@RoosterListener
object InventoryCloseListener : Listener {
    @EventHandler
    fun listener(event: InventoryCloseEvent) {
        if (cache.getIfPresent(InterfaceManager.CHANGES_INTERFACE_KEY, event.player) as Boolean? == true) {
            cache.invalidate(InterfaceManager.CHANGES_INTERFACE_KEY, event.player)
            return
        }
        InterfaceManager.setPlayerEmpty(event.player as Player)
    }
}