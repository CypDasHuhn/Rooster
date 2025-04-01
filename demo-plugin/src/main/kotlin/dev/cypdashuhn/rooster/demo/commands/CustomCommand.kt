package dev.cypdashuhn.rooster.demo.commands

import dev.cypdashuhn.rooster.commands.*
import dev.cypdashuhn.rooster.util.isPlayer

// /custom (example1|example2)
object CustomCommand : RoosterCommand("custom-command") {
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
            ).onExecute {
                val arg = context[key] as String
                sender.sendMessage("You selected $arg")
            }
        )
    }
}

object TestCommand : RoosterCommand("test-command") {
    override fun content(arg: UnfinishedArgument): Argument {
        return arg.onExecute {
            sender.sendMessage("msg")
        }
    }
}

val TestCommand2 = roosterCommand("test-command") {
    onExecute {
        sender.sendMessage("msg")
    }
}