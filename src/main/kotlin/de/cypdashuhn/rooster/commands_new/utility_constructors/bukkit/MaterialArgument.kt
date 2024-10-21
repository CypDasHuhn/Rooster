package de.cypdashuhn.rooster.commands_new.utility_constructors.bukkit

import de.cypdashuhn.rooster.commands_new.constructors.UnfinishedArgument
import de.cypdashuhn.rooster.commands_new.utility_constructors.ListArgument
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.Material

object MaterialArgument {
    fun single(
        key: String = "material",
        notMatchingKey: String = "material_not_matching",
        notMatchingArg: String = "material",
        onMissingKey: String = "material_missing",
        materialFilter: (Material) -> Boolean = { true }
    ): UnfinishedArgument {
        val materials = Material.entries.filter(materialFilter).map { it.name.lowercase() }

        return ListArgument.single(
            key = key,
            list = materials,
            notMatchingError = { info, material -> info.sender.tSend(notMatchingKey, notMatchingArg to material) },
            onMissing = { it.sender.tSend(onMissingKey) },
            transformValue = { info, materialString -> Material.valueOf(materialString.uppercase()) }
        )
    }

    fun multiple(
        key: String = "material",
        notMatchingKey: String = "material_not_matching",
        notMatchingArg: String = "material",
        onMissingKey: String = "material_missing",
        materialFilter: (Material) -> Boolean = { true }
    ): UnfinishedArgument {
        val materials = Material.entries.filter(materialFilter).map { it.name.lowercase() }

        return ListArgument.chainable(
            key = key,
            list = materials,
            notMatchingError = { info, material -> info.sender.tSend(notMatchingKey, notMatchingArg to material) },
            onMissing = { it.sender.tSend(onMissingKey) },
            transformValue = { info, materialString -> Material.valueOf(materialString.uppercase()) }
        )
    }
}