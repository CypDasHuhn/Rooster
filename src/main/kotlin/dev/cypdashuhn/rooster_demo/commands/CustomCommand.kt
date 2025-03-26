package dev.cypdashuhn.rooster_demo.commands

import dev.cypdashuhn.rooster.commands.Argument
import dev.cypdashuhn.rooster.commands.IsValidResult
import dev.cypdashuhn.rooster.commands.RoosterCommand
import dev.cypdashuhn.rooster.commands.UnfinishedArgument
import dev.cypdashuhn.rooster.util.isPlayer
import dev.cypdashuhn.rooster_ksp_processor.RoosterIgnore

// /custom (example1|example2)
object CustomCommand : RoosterCommand("custom-command") {
    override fun content(arg: UnfinishedArgument): dev.cypdashuhn.rooster.commands.Argument {
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

@RoosterIgnore
object TestCommand : RoosterCommand("test-command") {
    override fun content(arg: UnfinishedArgument): dev.cypdashuhn.rooster.commands.Argument {
        TODO("Not yet implemented")
    }

}