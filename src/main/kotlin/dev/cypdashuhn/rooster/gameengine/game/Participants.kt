package dev.cypdashuhn.rooster.gameengine.game

import org.bukkit.entity.Player

class ParticipantGroup(
    val name: String,
    val size: IntRange
) {
    companion object {
        fun list(size: IntRange, vararg names: String) = list(size, names.toList())

        fun list(size: Int, vararg names: String) = list(size..size, names.toList())

        fun list(size: IntRange, names: List<String>): List<ParticipantGroup> {
            return names.map { ParticipantGroup(it, size) }
        }

        fun list(size: Int, names: List<String>) = list(size..size, names)
    }
}

class Participant<T>(
    val player: Player,
    val groupName: String,
    val state: T
)