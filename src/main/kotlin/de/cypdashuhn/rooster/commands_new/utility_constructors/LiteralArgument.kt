package de.cypdashuhn.rooster.commands_new.utility_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands_new.constructors.ArgumentPredicate
import de.cypdashuhn.rooster.commands_new.constructors.IsValidResult
import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument

object LiteralArgument {
    fun single(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = null,
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

    fun multiple(
        names: List<String>,
        key: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate? = null,
        isValid: ((ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((ArgumentInfo) -> Unit)? = null,
        transformValue: ((ArgumentInfo) -> Any) = null,
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
}