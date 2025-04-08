package dev.cypdashuhn.rooster.commands.constructors

import dev.cypdashuhn.rooster.commands.*
import dev.cypdashuhn.rooster.core.Rooster
import dev.cypdashuhn.rooster.localization.t
import dev.cypdashuhn.rooster.localization.tSend
import org.bukkit.entity.Player

object LocalizationArgument {
    fun full(
        literalName: String = t("rooster.language.label"),
        onChange: InvokeInfo.() -> Unit = { sender.tSend("rooster.language.changed") },
        argKey: String = "language",
        onInvalidLanguage: (ArgumentInfo, String) -> Unit = playerMessageExtra(
            "rooster.language.invalid_error",
            argKey
        ),
        onMissingLanguage: ArgumentInfo.() -> Unit = playerMessage("rooster.language.missing_error")
    ): Argument {
        return Arguments.literal.single(literalName).followedBy(
            languageChanger(
                onChange, argKey, onInvalidLanguage, onMissingLanguage
            )
        )
    }

    fun languageChanger(
        onChange: InvokeInfo.() -> Unit = { sender.tSend("rooster.language.changed") },
        argKey: String = "language",
        onInvalidLanguage: (ArgumentInfo, String) -> Unit = playerMessageExtra(
            "rooster.language.invalid_error",
            argKey
        ),
        onMissingLanguage: ArgumentInfo.() -> Unit = playerMessage("rooster.language.missing_error")
    ): Argument {
        return Arguments.list.single(
            key = "language",
            isEnabled = { sender is Player },
            list = Rooster.localeProvider.getLanguageCodes(),
            notMatchingError = onInvalidLanguage,
            onMissing = onMissingLanguage
        ).onExecute {
            val arg = context["language"] as String
            Rooster.localeProvider.changeLanguage(sender as Player, arg)
            onChange(this)
        }
    }
}