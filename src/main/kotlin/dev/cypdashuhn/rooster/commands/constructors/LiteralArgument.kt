package dev.cypdashuhn.rooster.commands.constructors

import dev.cypdashuhn.rooster.commands.*
import dev.cypdashuhn.rooster.localization.language
import dev.cypdashuhn.rooster.localization.transformMessage

object LiteralArgument {
    fun single(
        name: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate = { transformMessage(name, it.sender.language()).startsWith(it.arg) },
        isValid: (ArgumentInfo.() -> IsValidResult)? = null,
        onMissing: (ArgumentInfo.() -> Unit)? = null,
        onMissingChild: (ArgumentInfo.() -> Unit)? = null,
        transformValue: (ArgumentInfo.() -> Any) = { arg },
        key: String = name,
    ): LiteralArgumentType {
        val arg = UnfinishedArgument(
            key = key,
            suggestions = { listOf(name) },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            isOptional = false
        )

        return LiteralArgumentType(arg, key)
    }

    fun multiple(
        names: List<String>,
        key: String,
        isEnabled: ArgumentPredicate? = null,
        isTarget: ArgumentPredicate = { names.contains(it.arg) },
        isValid: (ArgumentInfo.() -> IsValidResult)? = null,
        onMissing: (ArgumentInfo.() -> Unit)? = null,
        onMissingChild: (ArgumentInfo.() -> Unit)? = null,
        transformValue: (ArgumentInfo.() -> Any) = { arg },
    ): LiteralArgumentType {
        val arg = UnfinishedArgument(
            key = key,
            suggestions = { names },
            isEnabled = isEnabled,
            isTarget = isTarget,
            isValid = isValid,
            transformValue = transformValue,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            isOptional = false
        )

        return LiteralArgumentType(arg, key)
    }

    class LiteralArgumentType(arg: UnfinishedArgument, argKey: String) :
        SimpleArgumentType<String>("Literal", arg, argKey)
}