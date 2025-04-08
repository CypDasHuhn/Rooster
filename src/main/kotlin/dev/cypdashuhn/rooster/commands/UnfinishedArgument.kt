package dev.cypdashuhn.rooster.commands

open class UnfinishedArgument : BaseArgument {
    constructor(
        key: String,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        suggestions: (ArgumentInfo.() -> List<String>)? = null,
        onExecute: (InvokeInfo.() -> Unit)? = null,
        followedBy: List<BaseArgument>? = null,
        isValid: (ArgumentInfo.() -> IsValidResult)? = null,
        onMissing: (ArgumentInfo.() -> Unit)? = null,
        onMissingChild: (ArgumentInfo.() -> Unit)? = null,
        transformValue: (ArgumentInfo.() -> Any) = { arg },
        isOptional: Boolean = false,
    ) : super(
        key = key,
        isEnabled = isEnabled,
        isTarget = isTarget,
        suggestions = suggestions,
        onExecute = onExecute,
        followedBy = followedBy?.toMutableList(),
        isValid = isValid,
        onMissing = onMissing,
        onMissingChild = onMissingChild,
        transformValue = transformValue,
        isOptional = isOptional,
    )

    private constructor(
        key: String,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        suggestions: (ArgumentInfo.() -> List<String>)? = null,
        isValid: (ArgumentInfo.() -> IsValidResult)? = null,
        transformValue: (ArgumentInfo.() -> Any) = { arg },
    ) : super(
        key = key,
        isEnabled = isEnabled,
        isTarget = isTarget,
        suggestions = suggestions,
        onExecute = null,
        followedBy = null,
        isValid = isValid,
        onMissing = null,
        onMissingChild = null,
        transformValue = transformValue,
        isOptional = true,
        onArgumentOverflow = null,
    )

    internal companion object {
        fun optional(
            key: String,
            isEnabled: (ArgumentPredicate)? = { true },
            isTarget: (ArgumentPredicate) = { true },
            suggestions: (ArgumentInfo.() -> List<String>)? = null,
            isValid: (ArgumentInfo.() -> IsValidResult)? = null,
            transformValue: (ArgumentInfo.() -> Any) = { arg },
        ) {
            UnfinishedArgument(
                key = key,
                isEnabled = isEnabled,
                isTarget = isTarget,
                suggestions = suggestions,
                isValid = isValid,
                transformValue = transformValue,
            )
        }
    }

    open fun copy(): UnfinishedArgument {
        return toUnfinishedArgument()
    }

    fun <T> toTyped(): TypedArgument<T> {
        return SimpleArgumentType("Simple", this, this.key)
    }
}