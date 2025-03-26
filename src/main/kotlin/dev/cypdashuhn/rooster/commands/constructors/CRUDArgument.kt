package dev.cypdashuhn.rooster.commands.constructors

import dev.cypdashuhn.rooster.commands.Argument
import dev.cypdashuhn.rooster.commands.Arguments
import dev.cypdashuhn.rooster.commands.UnfinishedArgument
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Column

class CRUDArgument<T : IntEntity>(
    entity: IntEntityClass<T>,
    displayField: Column<String>
) {
    val newNameArgument = dev.cypdashuhn.rooster.commands.Arguments.names.unique(displayField)
    val nameArgument = dev.cypdashuhn.rooster.commands.Arguments.list.dbList(entity, displayField)

    fun arg(arg: UnfinishedArgument): dev.cypdashuhn.rooster.commands.Argument {
        return arg.followedBy(
            dev.cypdashuhn.rooster.commands.Arguments.literal.single("create").followedBy(newNameArgument).onExecute { },
            dev.cypdashuhn.rooster.commands.Arguments.literal.single("edit").followedBy(nameArgument).onExecute { },
            dev.cypdashuhn.rooster.commands.Arguments.literal.single("delete").followedBy(nameArgument).onExecute { },
            dev.cypdashuhn.rooster.commands.Arguments.literal.single("get").followedBy(nameArgument).onExecute { },
        )
    }
}