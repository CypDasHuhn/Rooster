package de.cypdashuhn.rooster.commands_new.constructors

import de.cypdashuhn.rooster.commands_new.utility_constructors.LiteralArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.MultiLiteralArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.NumberArgument

object Arguments {
    val literal = LiteralArgument
    val multiLiteral = MultiLiteralArgument
    val number = NumberArgument
}

fun test() {
    val s = Arguments.multiLiteral
    val e = NumberArgument.middle()
}