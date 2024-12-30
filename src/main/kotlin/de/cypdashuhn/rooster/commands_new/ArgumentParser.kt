package de.cypdashuhn.rooster.commands_new

import de.cypdashuhn.rooster.commands_new.constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.constructors.BaseArgument
import de.cypdashuhn.rooster.commands_new.constructors.InvokeInfo
import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.core.Rooster.registeredRootArguments
import org.bukkit.command.CommandSender

object ArgumentParser {
    val defaultErrorArgumentsOverflow: (ArgumentInfo) -> Unit =
        { it.sender.sendMessage("Too many Arguments!") }

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
        var headArgument = topArgument.command as BaseArgument
        var onArgumentOverflow = headArgument.onArgumentOverflow
        var values: HashMap<String, Any?> = HashMap()

        var cachePosition: Int? = null
        val cacheInfo = cache.getIfPresent(CACHE_KEY, sender) as CacheInfo?

        if (cacheInfo != null && commandParseType == CommandParseType.TabCompleter) {
            if (cacheInfo.stringArguments.withoutLast()
                contentEquals
                stringArguments.withoutLast() ||
                cacheInfo.stringArguments
                contentEquals
                stringArguments.withoutLast()
            ) {
                arguments = cacheInfo.arguments
                headArgument = cacheInfo.headArgument
                onArgumentOverflow = cacheInfo.errorArgumentOverflow
                values = cacheInfo.values

                cachePosition = when {
                    stringArguments.last().isBlank() -> cacheInfo.stringArguments.size - 1
                    else -> cacheInfo.stringArguments.size - 1
                }
            }
        }

        for ((index, stringArgument) in stringArguments.withIndex()) {
            if (cachePosition != null && cachePosition > index) {
                continue
            }

            val argumentInfo = ArgumentInfo(
                sender,
                stringArguments,
                stringArgument,
                index,
                values
            )
            val cacheInfo = CacheInfo(
                stringArguments,
                arguments,
                headArgument,
                onArgumentOverflow,
                values
            )

            val currentTabCompletions: () -> List<String> = {
                arguments
                    .filter { (it.isEnabled == null || it.isEnabled!!(argumentInfo)) && it.suggestions != null }
                    .flatMap { it.suggestions!!(argumentInfo) }
            }

            val currentArgument = arguments.firstOrNull { arg -> arg.isTarget(argumentInfo) }
                ?: when (commandParseType) { // Null Handling
                    CommandParseType.TabCompleter -> {
                        cacheCommand(sender, cacheInfo)

                        return ReturnResult(currentTabCompletions())
                    }

                    ArgumentParser.CommandParseType.Invocation -> {
                        arguments.forEach { arg ->
                            arg.onMissing?.let {
                                return ReturnResult(success = false) {
                                    it(
                                        argumentInfo
                                    )
                                }
                            }
                        }
                        requireNotNull(headArgument.onMissingChild) { "Head Argument error passed. If head doesn't have message, Argument structure is wrong." }

                        return ReturnResult(success = false) {
                            headArgument.onMissingChild!!.invoke(argumentInfo)
                        }
                    }
                }

            currentArgument.onArgumentOverflow?.let { onArgumentOverflow = it }

            fun isValid(): ReturnResult? {
                if (currentArgument.isValid != null) {
                    val isValidResult = currentArgument.isValid!!(argumentInfo)

                    if (!isValidResult.isValid) {
                        return ReturnResult(success = false) { isValidResult.error!!(argumentInfo) }
                    }
                }
                values[currentArgument.key] = currentArgument.transformValue(argumentInfo)
                return null
            }


            if (commandParseType == CommandParseType.Invocation) {
                val result = isValid()
                if (result != null) return result
            }

            arguments // Each modifier not mentioned is set to false
                .filter { it.isOptional }
                .forEach { values.putIfAbsent(it.key, false) }

            val isLastElement = index == stringArguments.size - 1

            if (!isLastElement) {
                if (commandParseType == CommandParseType.TabCompleter) {
                    val result = isValid()
                    if (result != null) return result
                }

                if (currentArgument.isOptional) {
                    // step further (stay but without this one)
                    arguments.remove(currentArgument)
                } else if (currentArgument.followedBy != null) {
                    // step further
                    arguments = currentArgument.followedBy!!.toMutableList()
                    headArgument = currentArgument
                } else {
                    // to many arguments
                    return ArgumentParser.ReturnResult(success = false) {
                        onArgumentOverflow?.let { it(argumentInfo) }
                            ?: defaultErrorArgumentsOverflow(argumentInfo)
                    }
                }
                continue
            }

            // Only invoked if it's the last element
            cacheCommand(sender, cacheInfo)

            when (commandParseType) {
                CommandParseType.TabCompleter -> {
                    return ReturnResult(currentTabCompletions())
                }

                CommandParseType.Invocation -> {
                    val comparativeArgument = when (currentArgument.isOptional) {
                        true -> headArgument
                        false -> currentArgument
                    }
                    if (comparativeArgument.onExecute != null) {
                        return ReturnResult(success = true) {
                            comparativeArgument.onExecute!!(InvokeInfo(sender, values))
                        }
                    }

                    // error handling

                    requireNotNull(comparativeArgument.followedBy) {
                        "Current argument needs to have following arguments if invoke doesn't exist"
                    }

                    comparativeArgument.onMissingChild?.let {
                        return ReturnResult(success = false) { it(argumentInfo) }
                    }

                    val inferiorArguments = when (comparativeArgument.isOptional) {
                        true -> arguments.also { it.remove(comparativeArgument) }
                        false -> comparativeArgument.followedBy!!
                    }

                    val firstArgumentWithError = inferiorArguments.firstOrNull { it.onMissing != null }
                    requireNotNull(firstArgumentWithError) {
                        "At least one Argument should have an error Message, else the Parent would need to have one registered"
                    }

                    return ReturnResult(success = false) { firstArgumentWithError.onMissing!!(argumentInfo) }

                }
            }
        }
        return errorWithoutInfo
    }

    private fun cacheCommand(
        sender: CommandSender,
        cacheInfo: CacheInfo
    ) {
        cache.put(
            CACHE_KEY,
            sender,
            cacheInfo,
        )
    }
}
