package de.cypdashuhn.rooster.commands_new.utility_constructors.crud

import de.cypdashuhn.rooster.commands_new.constructors.*
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere

data class CreateInfo<T : Any>(
    val method: (ArgumentInfo, String, T) -> Unit,
    val name: String = "create"
)

data class DeleteInfo<T : IntEntity>(
    val method: (ArgumentInfo, String, T) -> Unit = {
        
        T.deleteWhere { nameColumn eq name }
    },
    val name: String = "delete"
)

data class ReadInfo<T : Any>(
    val method: (ArgumentInfo, String, T) -> Unit,
    val name: String = "read"
)

data class UpdateInfo<T : Any>(
    val method: (ArgumentInfo, String, T) -> Unit,
    val name: String = "update"
)

fun <E : IntEntity> generate(
    nameColumn: Column<String>,
    entity: IntEntityClass<E>,
    infoArguments: UnfinishedArgument?,
    createInfo: CreateInfo<E>? = null,
    deleteInfo: DeleteInfo<E>? = null,
): List<Argument> {
    val table = entity.table
    val arguments = listOf<Argument>()

    val nameArgument = Arguments.list.db("name", table, nameColumn)

    if (createInfo != null) {
        var create: BaseArgument = Arguments.literal.single("create").followedBy(
            UnfinishedArgument(key = "name")
        )
        if (infoArguments != null) create = create.followedBy(infoArguments)
        create = create.onExecute {
            val name = it.context["name"] as String
            entity.new {
                createInfo.createMethod(it, name, this)
            }
        }
    }

    if

    var delete = Arguments.literal.single("delete").followedBy(nameArgument).onExecute {
        val name = it.context["name"] as String

    }


    return arguments
}

fun test() {
    val e = generate(
        PlayerManager.Players.name,
        PlayerManager.DbPlayer,
        null,
        CreateInfo({ argumentInfo, s, value -> })
    )
}