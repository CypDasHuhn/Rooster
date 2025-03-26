package dev.cypdashuhn.rooster.database.utility_tables

import dev.cypdashuhn.rooster.core.Rooster
import org.jetbrains.exposed.sql.Table

abstract class UtilityDatabase(table: Table) {
    init {
        Rooster.dynamicTables += table
    }
}