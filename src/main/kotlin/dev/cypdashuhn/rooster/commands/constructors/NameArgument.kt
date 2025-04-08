package dev.cypdashuhn.rooster.commands.constructors

import dev.cypdashuhn.rooster.commands.ArgumentInfo
import dev.cypdashuhn.rooster.commands.IsValidResult
import dev.cypdashuhn.rooster.commands.TypedArgument
import dev.cypdashuhn.rooster.commands.UnfinishedArgument
import dev.cypdashuhn.rooster.localization.t
import dev.cypdashuhn.rooster.localization.tSend
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object NameArgument {
    fun simple(
        key: String = "name",
        isValid: (ArgumentInfo.() -> IsValidResult)? = null
    ): TypedArgument<String> {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name.placeholder")) },
            isValid = isValid
        ).toTyped()
    }

    fun unique(
        usedNames: List<String>,
        key: String = "name",
        uniqueErrorKey: String = "rooster.name.reserved_error",
        isValid: (ArgumentInfo.() -> IsValidResult)? = null
    ): TypedArgument<String> {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name.placeholder")) },
            isValid = {
                if (usedNames.contains(arg)) {
                    IsValidResult.Invalid { sender.tSend(uniqueErrorKey) }
                } else {
                    isValid?.invoke(this) ?: IsValidResult.Valid()
                }
            }
        ).toTyped()
    }

    fun unique(
        targetColumn: Column<String>,
        extraQuery: Op<Boolean>? = null,
        isValid: (ArgumentInfo.() -> IsValidResult)? = null,
        key: String = "name",
        uniqueErrorKey: String = "rooster.name.reserved_error",
        nameArg: String = "name"
    ): TypedArgument<String> {
        return UnfinishedArgument(
            key = key,
            suggestions = { listOf(t("rooster.name.placeholder")) },
            isValid = {
                transaction {
                    val table = targetColumn.table
                    var query = targetColumn eq arg
                    extraQuery?.let { query = query and it }

                    if (table.selectAll().where { query }.firstOrNull() != null) {
                        IsValidResult.Invalid { sender.tSend(uniqueErrorKey, nameArg to arg) }
                    } else {
                        isValid?.invoke(this@UnfinishedArgument) ?: IsValidResult.Valid()
                    }
                }
            }
        ).toTyped()
    }
}