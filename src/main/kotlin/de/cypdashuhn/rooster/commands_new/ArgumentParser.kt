package de.cypdashuhn.rooster.commands_new

import de.cypdashuhn.rooster.commands_new.constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.constructors.BaseArgument
import de.cypdashuhn.rooster.core.Rooster.registeredRootArguments
import org.bukkit.command.CommandSender

object ArgumentParser {
    val defaultErrorArgumentOverflow: (ArgumentInfo) -> Unit = { it.sender.sendMessage("Too many Arguments!") }

    class ReturnResult {
        var success: Boolean
        var tabCompleterList: List<String>
        var invocationLambda: () -> Unit
        var errorInvocation: () -> Unit

        constructor(success: Boolean, lambda: () -> Unit) {
            this.success = success
            this.tabCompleterList = ArrayList()
            this.invocationLambda = if (success) lambda else {
                {}
            }
            this.errorInvocation = if (!success) lambda else {
                {}
            }
        }

        constructor(value: List<String>) {
            this.success = true
            this.tabCompleterList = value
            this.invocationLambda = {}
            this.errorInvocation = {}
        }

        constructor() {
            this.success = false
            this.tabCompleterList = ArrayList()
            this.invocationLambda = {}
            this.errorInvocation = {}
        }
    }

    enum class CommandParseType {
        TabCompleter,
        Invocation
    }

    const val CACHE_KEY = "rooster_arguments_cache"

    private fun Array<String>.withoutLast(): Array<String> {
        return this.copyOfRange(0, this.size - 1)
    }

    data class CacheInfo(
        val stringArguments: Array<String>,
        val arguments: MutableList<BaseArgument>,
        val headArgument: BaseArgument,
        val errorArgumentOverflow: ((ArgumentInfo) -> Unit)?,
        val values: HashMap<String, Any?>
    )

    fun parse(
        sender: CommandSender,
        label: String,
        rawStringArguments: Array<String>,
        commandParseType: CommandParseType
    ): ReturnResult {
        val errorWithoutInfo = ReturnResult()

        val topArgument = requireNotNull(
            registeredRootArguments.firstOrNull { it.labels.any { it.lowercase() == label.lowercase() } }
        ) { "Root must be found, else command invocation wouldn't be possible" }

        val continueArgument = topArgument.onStart(sender)
        if (!continueArgument) return errorWithoutInfo

        /* Prepends label to arguments
        "/label [...args]" -> [label, ...args]  */
        val stringArguments = rawStringArguments.toMutableList().also { it.add(0, label) }.toTypedArray()

        @Suppress("UNCHECKED_CAST")
        var arguments = mutableListOf(topArgument) as MutableList<BaseArgument>
        var headArgument = topArgument as BaseArgument
        var errorArgumentOverflow = topArgument.onArgumentOverflow
        var values: HashMap<String, Any?> = HashMap()
    }
}
