package de.cypdashuhn.rooster.commands_new.constructors

import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.utility_constructors.NumberArgument

object Arguments {
    fun literal(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any)? = null,
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = name,
            suggestions = { listOf(name) },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            onArgumentOverflow = onArgumentOverflow,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            isOptional = false
        )
    }

    fun literalMultiple(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any)? = null,
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        onExecute: (ArgumentInfo) -> Unit,
        followedBy: List<Argument>? = null
    ): Argument {
        return Argument(
            key = name,
            suggestions = { listOf(name) },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            onExecute = onExecute,
            onArgumentOverflow = onArgumentOverflow,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            followedBy = followedBy
        )
    }

    fun literalMultiple(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any)? = null,
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followedBy: List<Argument>
    ): Argument {
        return Argument(
            key = name,
            suggestions = { listOf(name) },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            followedBy = followedBy,
            onArgumentOverflow = onArgumentOverflow,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
        )
    }

    fun literal(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any)? = null,
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        onExecute: (ArgumentInfo) -> Unit,
        followedBy: Argument? = null
    ): Argument {
        return Argument(
            key = name,
            suggestions = { listOf(name) },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            onExecute = onExecute,
            onArgumentOverflow = onArgumentOverflow,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            followedBy = if (followedBy == null) null else listOf(followedBy)
        )
    }

    fun literal(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any)? = null,
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followedBy: Argument
    ): Argument {
        return Argument(
            key = name,
            suggestions = { listOf(name) },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            followedBy = listOf(followedBy),
            onArgumentOverflow = onArgumentOverflow,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
        )
    }

    val number = NumberArgument
}

fun test() {
    val s = Arguments.number.middle()
    val e = NumberArgument.middle()
}