package de.cypdashuhn.rooster.listeners

import de.cypdashuhn.rooster.core.RoosterServices
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("unused")
@RoosterListener
object PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        RoosterServices.getIfPresent<PlayerManager>()?.let {
            it.beforePlayerJoin(event)
            it.playerLogin(event.player)
            it.onPlayerJoin(event)
        }
    }
}