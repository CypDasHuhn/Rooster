package de.cypdashuhn.rooster.commands_new.utility_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentPredicate
import de.cypdashuhn.rooster.commands_new.constructors.Argument
import de.cypdashuhn.rooster.commands_new.constructors.IsValidResult
import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument

object MultiLiteralArgument {
    fun middle(
        names: List<String>,
        key: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any)? = null,
        onArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    ): UnfinishedArgument {
        return UnfinishedArgument(
            key = key,
            suggestions = { names },
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

    fun multiple(
        names: List<String>,
        key: String,
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
            key = key,
            suggestions = { names },
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

    fun multiple(
        names: List<String>,
        key: String,
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
            key = key,
            suggestions = { names },
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

    fun single(
        names: List<String>,
        key: String,
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
            key = key,
            suggestions = { names },
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

    fun single(
        names: List<String>,
        key: String,
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
            key = key,
            suggestions = { names },
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
}