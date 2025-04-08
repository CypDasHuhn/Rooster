package dev.cypdashuhn.rooster.commands

class Argument : BaseArgument {
    private constructor(
        key: String,
        isEnabled: (ArgumentPredicate)? = { true },
        isTarget: (ArgumentPredicate) = { true },
        suggestions: (ArgumentInfo.() -> List<String>)? = null,
        onExecute: (InvokeInfo.() -> Unit)? = null,
        followedBy: List<Argument>? = null,
        isValid: (ArgumentInfo.() -> IsValidResult)? = null,
        onMissing: (ArgumentInfo.() -> Unit)? = null,
        onMissingChild: (ArgumentInfo.() -> Unit)? = null,
        transformValue: (ArgumentInfo.() -> Any) = { arg },
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
        isOptional = false,
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
        ) = Argument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            isValid = isValid,
            transformValue = transformValue,
        )

        fun createWithExecute(
            key: String,
            isEnabled: (ArgumentPredicate)? = { true },
            isTarget: (ArgumentPredicate) = { true },
            suggestions: (ArgumentInfo.() -> List<String>)? = null,
            onExecute: InvokeInfo.() -> Unit,
            followedBy: List<Argument>? = null,
            isValid: (ArgumentInfo.() -> IsValidResult)? = null,
            onMissing: (ArgumentInfo.() -> Unit)? = null,
            onMissingChild: (ArgumentInfo.() -> Unit)? = null,
            transformValue: (ArgumentInfo.() -> Any) = { arg },
        ) = Argument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            onExecute = onExecute,
            followedBy = followedBy?.toMutableList(),
            isValid = isValid,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            transformValue = transformValue
        )

        fun createWithFollowing(
            key: String,
            isEnabled: (ArgumentPredicate)? = { true },
            isTarget: (ArgumentPredicate) = { true },
            suggestions: (ArgumentInfo.() -> List<String>)? = null,
            onExecute: (InvokeInfo.() -> Unit)? = null,
            followedBy: List<Argument>,
            isValid: (ArgumentInfo.() -> IsValidResult)? = null,
            onMissing: (ArgumentInfo.() -> Unit)? = null,
            onMissingChild: (ArgumentInfo.() -> Unit)? = null,
            transformValue: (ArgumentInfo.() -> Any) = { arg },
        ) = Argument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            onExecute = onExecute,
            followedBy = followedBy.toMutableList(),
            isValid = isValid,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            transformValue = transformValue
        )

        fun create(
            key: String,
            isEnabled: (ArgumentPredicate)? = { true },
            isTarget: (ArgumentPredicate) = { true },
            suggestions: (ArgumentInfo.() -> List<String>)? = null,
            onExecute: (InvokeInfo.() -> Unit)? = null,
            followedBy: List<Argument>,
            isValid: (ArgumentInfo.() -> IsValidResult)? = null,
            onMissing: (ArgumentInfo.() -> Unit)? = null,
            onMissingChild: (ArgumentInfo.() -> Unit)? = null,
            transformValue: (ArgumentInfo.() -> Any) = { arg },
        ) = Argument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            onExecute = onExecute,
            followedBy = followedBy.toMutableList(),
            isValid = isValid,
            onMissing = onMissing,
            onMissingChild = onMissingChild,
            transformValue = transformValue
        )
    }

    infix fun or(alternative: Argument): Argument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = mutableListOf(alternative)
            } else {
                it.followedBy!!.add(alternative)
            }
        } as Argument
    }

    fun or(vararg alternatives: Argument): Argument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = alternatives.toMutableList()
            } else {
                it.followedBy!!.addAll(alternatives)
            }
        } as Argument
    }

    infix fun or(alternatives: List<Argument>): Argument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = alternatives.toMutableList()
            } else {
                it.followedBy!!.addAll(alternatives)
            }
        } as Argument
    }

    infix fun or(alternative: UnfinishedArgument): UnfinishedArgument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = mutableListOf(alternative)
            } else {
                it.followedBy!!.add(alternative)
            }
        }.toUnfinishedArgument()
    }

    fun copy(): Argument {
        return toArgument()
    }

    fun transformByKey(key: String, block: (Argument) -> Argument): List<Argument> {
        val transformedNode = if (this.key == key) block(this) else this

        val transformedChildren = this.followedBy?.flatMap { child ->
            (child as Argument).transformByKey(key, block)
        } ?: emptyList()

        val newNode = transformedNode.copy(followedBy = transformedChildren)

        return listOf(newNode)
    }
}