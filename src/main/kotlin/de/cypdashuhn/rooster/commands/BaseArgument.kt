package de.cypdashuhn.rooster.commands

import org.bukkit.command.CommandSender

open class InvokeInfo(
    open val sender: CommandSender,
    open val context: CommandContext,
    open val args: List<String>,
) {
    fun <T> arg(argType: TypedArgument<T>): T {
        return when (val result = argType.value(sender, context)) {
            is TypeResult.Success -> result.value
            is TypeResult.Failure -> {
                result.action()
                throw result.exception
            }
        }
    }

    fun <T> arg(argTypes: List<TypedArgument<T>>): T {
        for (argType in argTypes) {
            val isLast = argTypes.last() == argType

            if (!isLast) {
                when (val result = argNullable(argType)) {
                    is TypeResult.Success<*> -> return result
                    is TypeResult.Failure<*> -> continue
                }
            }

            return arg(argType)
        }

        throw IllegalStateException("Should not hit")
    }

    fun <T> argNullable(argType: TypedArgument<T>): T? {
        return when (val result = argType.value(sender, context)) {
            is TypeResult.Success -> result.value
            is TypeResult.Failure -> return null
        }
    }

    fun <T> argNullable(argTypes: List<TypedArgument<T>>): T? {
        for (argType in argTypes) {
            when (val result = argNullable(argType)) {
                is TypeResult.Success<*> -> return result
                is TypeResult.Failure<*> -> continue
            }
        }

        return null
    }
}

data class ArgumentInfo(
    override val sender: CommandSender,
    override val args: List<String>,
    val arg: String,
    val index: Int,
    override val context: CommandContext
) : InvokeInfo(sender, context, args)

typealias ArgumentPredicate = (ArgumentInfo) -> Boolean

abstract class BaseArgument(
    open var key: String,
    open var isEnabled: (ArgumentPredicate)? = { true },
    open var isTarget: (ArgumentPredicate) = { true },
    open var suggestions: ((ArgumentInfo) -> List<String>)? = null,
    open var onExecute: ((InvokeInfo) -> Unit)? = null,
    open var followedBy: MutableList<BaseArgument>? = null,
    open var isValid: ((ArgumentInfo) -> IsValidResult)? = null,
    open var onMissing: ((ArgumentInfo) -> Unit)? = null,
    open var onMissingChild: ((ArgumentInfo) -> Unit)? = null,
    open var transformValue: ((ArgumentInfo) -> Any) = { it.arg },
    open var isOptional: Boolean = false,
    open var onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    internal var internalLastChange: BaseArgument? = null
) {
    protected fun toArgument(): Argument {
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
        ).also { it.internalLastChange = internalLastChange }
    }

    fun displayPaths(): List<String> {
        val paths = mutableListOf<String>()

        fun traverse(node: BaseArgument, currentPath: String) {
            val newPath = if (currentPath.isEmpty()) node.key else "$currentPath ${node.key}"

            if (node.onExecute != null || node.followedBy == null) {
                paths.add(newPath)
            }

            // Recursively traverse children if not null
            node.followedBy?.forEach { child ->
                traverse(child, newPath)
            }
        }

        traverse(this, "")
        return paths
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


    fun onExecute(onExecute: ((InvokeInfo) -> Unit)): Argument {
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

    fun onMissing(onMissing: ((ArgumentInfo) -> Unit)?): Argument {
        return appendChange { it.onMissing = onMissing }.toArgument()
    }

    fun onMissingChild(onMissingChild: ((ArgumentInfo) -> Unit)?): Argument {
        return appendChange { it.onMissingChild = onMissingChild }.toArgument()
    }

    fun onArgumentOverflow(onArgumentOverflow: ((ArgumentInfo) -> Unit)?): Argument {
        return appendChange { it.onArgumentOverflow = onArgumentOverflow }.toArgument()
    }

    fun isValid(isValid: ((ArgumentInfo) -> IsValidResult)?): Argument {
        return appendChange { it.isValid = isValid }.toArgument()
    }

    fun isEnabled(isEnabled: (ArgumentPredicate)?): Argument {
        return appendChange { it.isEnabled = isEnabled }.toArgument()
    }

    fun byKey(key: String): List<BaseArgument> {
        fun arg(arg: BaseArgument): List<BaseArgument> {
            val s = if (this.key == key) listOf(this) else listOf()
            val c = (this.followedBy?.map { arg(it) } ?: listOf()).flatten()
            return s + c
        }
        return arg(this)
    }

    fun byKey(key: String, s: () -> BaseArgument) {

    }
}

internal fun <T : BaseArgument> T.appendChange(changeArgument: (BaseArgument) -> Unit): T {
    var currentArgument: BaseArgument = this
    var count = 0

    while (currentArgument.followedBy != null) {
        currentArgument = currentArgument.followedBy!!.last()
        if (count++ >= 1000) {
            throw IllegalStateException("Infinite Loop")
        }
    }
    changeArgument(currentArgument)
    this.internalLastChange = currentArgument
    return this
}

fun List<BaseArgument>.eachOnExecute(onExecute: (InvokeInfo) -> Unit): List<Argument> {
    return this.map {
        it.onExecute(onExecute)
    }
}

fun List<BaseArgument>.eachFollowedBy(followedBy: List<Argument>): List<Argument> {
    return this.map { arg ->
        arg.followedBy(followedBy)
    }
}

fun List<BaseArgument>.eachFollowedBy(vararg followedBy: Argument): List<Argument> {
    return eachFollowedBy(followedBy.toList())
}

fun List<BaseArgument>.eachFollowedBy(followedBy: UnfinishedArgument): List<UnfinishedArgument> {
    return this.map {
        it.followedBy(followedBy)
    }
}

infix fun List<Argument>.or(followedBy: List<Argument>): List<Argument> {
    return this + followedBy
}

infix fun List<Argument>.or(followedBy: UnfinishedArgument): List<UnfinishedArgument> {
    return (this as List<BaseArgument> + followedBy as BaseArgument).map { it.toUnfinishedArgument() }
}

infix fun List<Argument>.or(followedBy: Argument): List<Argument> {
    return this + followedBy
}

infix fun List<UnfinishedArgument>.or(followedBy: BaseArgument): List<UnfinishedArgument> {
    return this + followedBy.toUnfinishedArgument()
}