package de.cypdashuhn.rooster.database.utility_tables

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Players.uuid
import org.bukkit.Bukkit
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import de.cypdashuhn.rooster.uuid

/**
 * Not Completely Necessary. Use BukkitAPI instead. This manager is if your
 * call frequency exceeds API Limitations, or whatever else you'd like to
 * do.
 */
open class PlayerManager {
    init {
        Rooster.dynamicTables += Players

        Rooster.playerManagerLogin = { playerLogin(it) }
    }

    object Players : IntIdTable() {
        val uuid = varchar("uuid", 36)
        val name = varchar("name", 16)
        val lastLogin = long("last_login")
    }

    class Player(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Player>(Players)

        var uuid by Players.uuid
        var name by Players.name
        var lastLogin by Players.lastLogin

        val online
            get() = bukkitPlayer != null

        val bukkitPlayer: org.bukkit.entity.Player?
            get() = Bukkit.getWorlds().flatMap { it.players }.first { it.uuid() == uuid }
    }


    fun playerLogin(player: org.bukkit.entity.Player) {
        return transaction {
            Player.find { uuid eq player.uuid() }.firstOrNull()?.delete()

            Players.insert {
                it[uuid] = player.uuid()
                it[name] = player.name
                it[lastLogin] = player.lastLogin
            }
        }
    }

    fun playerByUUID(uuid: String): Player? {
        return transaction { Player.find { Players.uuid eq uuid }.firstOrNull() }
    }

    fun playerByName(name: String): Player? {
        return transaction { Player.find { Players.name eq name }.firstOrNull() }
    }

    fun players(): List<Player> {
        return transaction { Player.all().toList() }
    }
}