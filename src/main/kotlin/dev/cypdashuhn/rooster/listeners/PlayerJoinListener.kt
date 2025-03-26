package dev.cypdashuhn.rooster.listeners

import dev.cypdashuhn.rooster.core.RoosterServices
import dev.cypdashuhn.rooster.database.utility_tables.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("unused")
object PlayerJoinListener : RoosterListener() {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        RoosterServices.getIfPresent<PlayerManager>()?.let {
            it.beforePlayerJoin(event)
            it.playerLogin(event.player)
            it.onPlayerJoin(event)
        }
    }
}