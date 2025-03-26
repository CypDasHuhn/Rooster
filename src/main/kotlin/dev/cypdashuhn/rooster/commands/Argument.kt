package dev.cypdashuhn.rooster.commands

class Argument : _root_ide_package_.dev.cypdashuhn.rooster.commands.BaseArgument {
    constructor(
        key: String,
        isEnabled: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate)? = { true },
        isTarget: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate) = { true },
        suggestions: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> List<String>)? = null,
        onExecute: (_root_ide_package_.dev.cypdashuhn.rooster.commands.InvokeInfo) -> Unit,
        followedBy: List<Argument>? = null,
        isValid: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Unit)? = null,
        transformValue: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Any) = { it.arg },
        toBeDeleted: Boolean = false
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

    constructor(
        key: String,
        isEnabled: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate)? = { true },
        isTarget: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate) = { true },
        suggestions: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> List<String>)? = null,
        onExecute: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.InvokeInfo) -> Unit)? = null,
        followedBy: List<Argument>,
        isValid: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> IsValidResult)? = null,
        onMissing: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Unit)? = null,
        onMissingChild: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Unit)? = null,
        transformValue: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Any) = { it.arg },
    ) : super(
        key = key,
        isEnabled = isEnabled,
        isTarget = isTarget,
        suggestions = suggestions,
        onExecute = onExecute,
        followedBy = followedBy.toMutableList(),
        isValid = isValid,
        onMissing = onMissing,
        onMissingChild = onMissingChild,
        transformValue = transformValue,
        isOptional = false,
    )

    private constructor(
        key: String,
        isEnabled: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate)? = { true },
        isTarget: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate) = { true },
        suggestions: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> List<String>)? = null,
        isValid: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> IsValidResult)? = null,
        transformValue: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Any) = { it.arg },
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
            isEnabled: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate)? = { true },
            isTarget: (_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentPredicate) = { true },
            suggestions: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> List<String>)? = null,
            isValid: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> IsValidResult)? = null,
            transformValue: ((_root_ide_package_.dev.cypdashuhn.rooster.commands.ArgumentInfo) -> Any) = { it.arg },
        ) = Argument(
            key = key,
            isEnabled = isEnabled,
            isTarget = isTarget,
            suggestions = suggestions,
            isValid = isValid,
            transformValue = transformValue,
        )
    }

    fun or(alternative: Argument): Argument {
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

    fun or(alternatives: List<Argument>): Argument {
        return appendAtLastChange {
            if (it.followedBy == null) {
                it.followedBy = alternatives.toMutableList()
            } else {
                it.followedBy!!.addAll(alternatives)
            }
        } as Argument
    }

    fun or(alternative: UnfinishedArgument): UnfinishedArgument {
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
}