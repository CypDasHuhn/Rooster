package dev.cypdashuhn.rooster.commands

import dev.cypdashuhn.rooster.commands.constructors.ListArgument
import dev.cypdashuhn.rooster.commands.constructors.LiteralArgument
import dev.cypdashuhn.rooster.commands.constructors.NameArgument
import dev.cypdashuhn.rooster.commands.constructors.NumberArgument
import dev.cypdashuhn.rooster.commands.constructors.bukkit.LocationArgument
import dev.cypdashuhn.rooster.commands.constructors.bukkit.MaterialArgument
import dev.cypdashuhn.rooster.commands.constructors.bukkit.PlayerArgument
import dev.cypdashuhn.rooster.commands.constructors.bukkit.WorldArgument

object Arguments {
    val literal = LiteralArgument
    val number = NumberArgument
    val list = ListArgument
    val names = NameArgument

    val material = MaterialArgument
    val world = WorldArgument
    val player = PlayerArgument
    val location = LocationArgument

    fun branch(map: Map<String, Argument>): List<Argument> {
        return map.map { (key, arg) ->
            literal.single(key).followedBy(arg)
        }
    }
}