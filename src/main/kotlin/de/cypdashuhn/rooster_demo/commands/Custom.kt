package de.cypdashuhn.rooster_demo.commands

import de.cypdashuhn.rooster.commands.Argument
import de.cypdashuhn.rooster.commands.IsValidResult
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.UnfinishedArgument
import de.cypdashuhn.rooster.util.isPlayer

// /custom (example1|example2)
object CustomCommand : RoosterCommand("custom") {
    override fun content(arg: UnfinishedArgument): Argument {
        val options = listOf("example1", "example2")
        val key = "exampleKey"

        return arg.followedBy(
            UnfinishedArgument(
                key = key,
                isEnabled = { it.sender.isPlayer() },
                suggestions = { options },
                isValid = {
                    if (it.arg in options) IsValidResult.Valid()
                    else IsValidResult.Invalid { info -> info.sender.sendMessage("invalid") }
                },
                onMissing = { info -> info.sender.sendMessage("Please specify an argument") },
            ).onExecute { info ->
                val arg = info.context[key] as String
                info.sender.sendMessage("You selected $arg")
            }
        )
    }

}