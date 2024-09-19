package de.cypdashuhn.rooster.simulator

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.commands.ArgumentParser
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object CommandSimulator {
    fun commandInvoke(command: String, player: Player) {
        val result = command(command, ArgumentParser.CommandParseType.Invocation, player)

        println("Success: ${result.success}")
        if (result.success) {
            result.invocationLambda()
        }
    }
    fun commandComplete(command: String, player: Player) {
        val result = command(command, ArgumentParser.CommandParseType.TabCompleter, player)

        println("Success: ${result.success}")
        if (result.success) {
            println("Completions: ")
            result.tabCompleterList.forEach {
                println("# $it")
            }
        }
    }

    fun command(command: String, type: ArgumentParser.CommandParseType, player: Player): ArgumentParser.ReturnResult {
        val (label, args) = commandTokenized(command)

        if (!Rooster.rootArguments.any { it.label == label }) {
            println("Unknown command: $command")
            return ArgumentParser.ReturnResult()
        }

        return ArgumentParser.parse(player, label, args, type)
    }

    fun commandTokenized(command: String): Pair<String, Array<String>> {
        var command = command.trim()
        if (command.startsWith("/")) {
            command = command.substring(1)
        }
        val parts = command.split(" ").toMutableList()
        val label = parts.firstOrNull() ?: ""
        val args = parts.drop(1)
        parts.add(" ")

        return label to args.toTypedArray()
    }
}