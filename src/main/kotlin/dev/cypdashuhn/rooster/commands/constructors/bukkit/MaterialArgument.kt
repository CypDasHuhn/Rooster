package dev.cypdashuhn.rooster.commands.constructors.bukkit

import dev.cypdashuhn.rooster.commands.UnfinishedArgument
import dev.cypdashuhn.rooster.commands.constructors.ListArgument
import dev.cypdashuhn.rooster.commands.playerMessageExtra
import dev.cypdashuhn.rooster.localization.tSend
import org.bukkit.Material

object MaterialArgument {
    fun single(
        key: String = "material",
        notMatchingMessage: String = "rooster.material.not_matching_error",
        materialPlaceholderKey: String = "material",
        onMissingMessage: String = "rooster.material.missing_error",
        materialFilter: ((Material) -> Boolean)? = null
    ): UnfinishedArgument {
        val materials = Material.entries
            .run { if (materialFilter != null) this.filter(materialFilter) else this }
            .map { it.name.lowercase() }

        return ListArgument.single(
            key = key,
            list = materials,
            notMatchingError = playerMessageExtra(notMatchingMessage, materialPlaceholderKey),
            onMissing = { sender.tSend(onMissingMessage) },
            transformValue = { info, materialString -> Material.valueOf(materialString.uppercase()) }
        )
    }

    fun multiple(
        key: String = "material",
        notMatchingMessage: String = "rooster.material.not_matching_error",
        materialPlaceholder: String = "material",
        onMissingMessage: String = "rooster.material.missing_error",
        materialFilter: ((Material) -> Boolean)? = null
    ): UnfinishedArgument {
        val materials = Material.entries
            .run { if (materialFilter != null) this.filter(materialFilter) else this }
            .map { it.name.lowercase() }

        return ListArgument.chainable(
            key = key,
            list = materials,
            notMatchingError = playerMessageExtra(notMatchingMessage, materialPlaceholder),
            onMissing = { sender.tSend(onMissingMessage) },
            transformValue = { info, materialString -> Material.valueOf(materialString.uppercase()) }
        )
    }
}

/*
notMatchingError = { info, material ->
                info.sender.tSend(
                    notMatchingMessage,
                    notMatchingPlaceholder to material
                )
            },
 */