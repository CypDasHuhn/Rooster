package de.cypdashuhn.rooster.commands_new.constructors

import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentInfo
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.command.CommandSender

data class InvokeInfo(
    val sender: CommandSender,
    val context: Map<String, Any>
)

data class ArgumentInfo(
    val sender: CommandSender,
    val arg: String,
    val context: Map<String, Any>
)

typealias ArgumentPredicate = (ArgumentInfo) -> Boolean

abstract class BaseArgument(
    open var key: String,
    open var isEnabled: (ArgumentPredicate)? = { true },
    open var isTarget: (ArgumentPredicate)? = { true },
    open var suggestions: ((ArgumentInfo) -> List<String>)? = null,
    open var onExecute: ((ArgumentInfo) -> Unit)? = null,
    open var followedBy: MutableList<BaseArgument>? = null,
    open var isValid: ((ArgumentInfo) -> IsValidResult)? = null,
    open var onMissing: ((ArgumentInfo) -> Unit)? = null,
    open var onMissingChild: ((ArgumentInfo) -> Unit)? = null,
    open var transformValue: ((ArgumentInfo) -> Any)? = { it.arg },
    open var isOptional: Boolean = false,
    open var onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    internal var internalLastChange: BaseArgument? = null
) {
    private fun toArgument(): Argument {
        if (this is Argument) return this
        return if (isOptional) Argument.optional(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            isValid = isValid,
            transformValue = transformValue,
        ).also { it.internalLastChange = internalLastChange }
        else if (followedBy == null) {
            Argument(
                key = key,
                isEnabled = isEnabled,
                isTarget = isTarget,
                suggestions = suggestions,
                onExecute = onExecute!!,
                followedBy = null,
                isValid = isValid,
                onMissing = onMissing,
                onMissingChild = onMissingChild,
                transformValue = transformValue,
                onArgumentOverflow = onArgumentOverflow,
            ).also { it.internalLastChange = internalLastChange }
        } else {
            Argument(
                key = key,
                isEnabled = isEnabled,
                isTarget = isTarget,
                suggestions = suggestions,
                onExecute = onExecute,
                followedBy = followedBy!!.map { it.toArgument() },
                isValid = isValid,
                onMissing = onMissing,
                onMissingChild = onMissingChild,
                transformValue = transformValue,
                onArgumentOverflow = onArgumentOverflow,
            ).also { it.internalLastChange = internalLastChange }
        }
    }

    internal fun toUnfinishedArgument(): UnfinishedArgument {
        if (this is UnfinishedArgument) return this
        return UnfinishedArgument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            onExecute = onExecute,
            followedBy = followedBy,
            isValid = isValid,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            transformValue = transformValue,
            isOptional = isOptional,
            onArgumentOverflow = onArgumentOverflow,
        ).also { it.internalLastChange = internalLastChange }
    }

    private fun appendChange(changeArgument: (BaseArgument) -> Unit): BaseArgument {
        var currentArgument: BaseArgument = this
        var count = 0
        while (true) {
            if (currentArgument.followedBy == null) {
                changeArgument(currentArgument)
                this.internalLastChange = currentArgument
                return this
            } else {
                currentArgument = currentArgument.followedBy!!.last()
            }
            if (count < 1000) count++ else throw IllegalStateException("Infinite Loop")
        }
    }

    protected fun appendAtLastChange(changeArgument: (BaseArgument) -> Unit): BaseArgument {
        var currentArgument: BaseArgument = this
        var noChild = false

        var count = 0
        while (true) {
            if (currentArgument.key == internalLastChange?.key ||
                ((internalLastChange == null || noChild) && currentArgument.followedBy?.last()?.followedBy == null)
            ) {
                changeArgument(currentArgument)
                return this
            } else {
                currentArgument = currentArgument.followedBy?.last() ?: currentArgument.also { noChild = true }
            }

            if (count < 1000) count++ else throw IllegalStateException("Infinite Loop")
        }
    }


    fun onExecute(onExecute: ((ArgumentInfo) -> Unit)): Argument {
        return appendChange { it.onExecute = onExecute }.toArgument()
    }

    fun followedBy(followedBy: List<Argument>): Argument {
        return appendChange { it.followedBy = followedBy.toMutableList() }.toArgument()
    }

    fun followedBy(followedBy: UnfinishedArgument): UnfinishedArgument {
        return appendChange { it.followedBy = mutableListOf(followedBy) } as UnfinishedArgument
    }

    fun followedBy(followedBy: Argument): Argument {
        return appendChange { it.followedBy = mutableListOf(followedBy) }.toArgument()
    }

    fun followedBy(vararg followedBy: Argument): Argument {
        return appendChange { it.followedBy = followedBy.toMutableList() }.toArgument()
    }
}


sealed class IsValidResult(
    val isValid: Boolean,
    val error: ((ArgumentInfo) -> Unit)? = null
) {
    class Valid : IsValidResult(true, null)
    class Invalid(error: ((ArgumentInfo) -> Unit)) : IsValidResult(false, error)
}

object TestCommand : RoosterCommand("Test") {
    override fun content(arg: UnfinishedArgument): Argument {
        return arg
            .onExecute { it.sender.tSend("test") }
            .followedBy(Arguments.literal.single("player"))
            .followedBy(
                Arguments.literal.single("branch1").followedBy(Arguments.literal.single("deeper")).onExecute { })
            .or(Arguments.literal.single("branchTwo")).onExecute { }
    }

}
