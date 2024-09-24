package de.cypdashuhn.rooster.ui.context

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.findEntry
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DatabaseInterfaceContextProvider : InterfaceContextProvider() {
    init {
        Rooster.dynamicTables += InterfaceContexts
    }

    object InterfaceContexts : IntIdTable("RoosterInterfaceContexts") {
        val playerUUID = varchar("player_uuid", 50)
        val interfaceName = varchar("interface_name", 50)
        val content = text("content")
    }

    class InterfaceContext(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<InterfaceContext>(InterfaceContexts)

        val content by InterfaceContexts.content
    }

    override fun <T : Context> updateContext(player: Player, interfaceInstance: Interface<T>, context: T) {
        val jsonContent = Gson().toJson(context)
        transaction {
            val existingContext = InterfaceContexts.selectAll()
                .where { (InterfaceContexts.playerUUID eq player.uuid()) and (InterfaceContexts.interfaceName eq interfaceInstance.interfaceName) }
                .singleOrNull()

            if (existingContext != null) {
                InterfaceContexts.update({ InterfaceContexts.id eq existingContext[InterfaceContexts.id] }) {
                    it[content] = jsonContent
                }
            } else {
                InterfaceContexts.insert {
                    it[playerUUID] = player.uuid()
                    it[interfaceName] = interfaceInstance.interfaceName
                    it[content] = jsonContent
                }
            }
        }
    }

    override fun <T : Context> getContext(player: Player, interfaceInstance: Interface<T>): T? {
        val data = InterfaceContext.findEntry(
            (InterfaceContexts.playerUUID eq player.uuid()) and
                    (InterfaceContexts.interfaceName eq interfaceInstance.interfaceName)
        ) ?: return null

        val gson = GsonBuilder().create()
        return gson.fromJson(data.content, interfaceInstance.contextClass.java)
    }
}