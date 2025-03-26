package dev.cypdashuhn.rooster.listeners.chat

import dev.cypdashuhn.rooster.listeners.RoosterListener
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler

object ChatListener : RoosterListener() {
    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        ChatManager.callListener(event)
    }
}