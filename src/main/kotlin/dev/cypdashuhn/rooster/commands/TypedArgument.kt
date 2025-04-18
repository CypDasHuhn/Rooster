package dev.cypdashuhn.rooster.commands

import org.bukkit.command.CommandSender

abstract class TypedArgument<T>(
    internal val argument: UnfinishedArgument,
) : UnfinishedArgument(
    key = argument.key,
    isEnabled = argument.isEnabled,
    isTarget = argument.isTarget,
    suggestions = argument.suggestions,
    onExecute = argument.onExecute,
    followedBy = argument.followedBy,
    isValid = argument.isValid,
    onMissing = argument.onMissing,
    onMissingChild = argument.onMissingChild,
    transformValue = argument.transformValue,
    isOptional = argument.isOptional,
) {
    abstract fun value(sender: CommandSender, context: CommandContext): TypeResult<T>

    fun onExecuteWithThis(onExecuteCallback: (InvokeInfo, TypedArgument<T>) -> Unit): TypedArgument<T> {
        return this.onExecuteTyped { onExecuteCallback(this, this@TypedArgument) }
    }


    fun onExecuteTyped(onExecute: (InvokeInfo.() -> Unit)): TypedArgument<T> {
        return appendChange { it.onExecute = onExecute }
    }

    fun onExecuteWithThisFinished(onExecuteCallback: InvokeInfo.(TypedArgument<T>) -> Unit): Argument {
        return this.onExecute { onExecuteCallback(this@TypedArgument) }
    }

    override fun copy(): TypedArgument<T> {
        return this
    }
}

fun <T> List<TypedArgument<T>>.eachOnExecuteWithSelf(onExecuteCallback: InvokeInfo.(TypedArgument<T>) -> Unit): List<Argument> {
    return this.map { arg -> arg.onExecute { onExecuteCallback(arg) } }
}

fun <T> List<TypedArgument<T>>.eachOnExecuteWithSelfUnfinished(onExecuteCallback: (InvokeInfo, TypedArgument<T>) -> Unit): List<TypedArgument<T>> {
    return this.map { arg -> arg.onExecuteTyped { onExecuteCallback(this, arg) } }
}

sealed class TypeResult<T> {
    class Success<T>(val value: T) : TypeResult<T>()
    class Failure<T>(val exception: Exception, val action: () -> Unit = {}) : TypeResult<T>()
}

open class SimpleArgumentType<T>(val name: String, arg: UnfinishedArgument, open val argKey: String) :
    TypedArgument<T>(arg) {
    override fun value(sender: CommandSender, context: CommandContext): TypeResult<T> {
        val data = context[argKey] as? T?
            ?: return TypeResult.Failure(IllegalStateException("Data is null for $name Argument with the Key $argKey"))

        return TypeResult.Success(data)
    }
}

fun <T, E> TypedArgument<T>.adapt(
    adapter: (T) -> E?,
    adapterFailure: () -> TypeResult.Failure<E> = { TypeResult.Failure(IllegalStateException("Adapter failed")) }
): TypedArgument<E> {
    val self = this
    return object : TypedArgument<E>(argument) {
        override fun value(sender: CommandSender, context: CommandContext): TypeResult<E> {
            val result = self.value(sender, context)

            if (result is TypeResult.Failure) return TypeResult.Failure(result.exception, result.action)
            val value = (result as TypeResult.Success).value

            adapter(value)
            val adapted = adapter(value) ?: return adapterFailure()
            return TypeResult.Success(adapted)
        }
    }
}