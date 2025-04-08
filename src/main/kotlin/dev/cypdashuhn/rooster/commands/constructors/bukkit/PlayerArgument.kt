package dev.cypdashuhn.rooster.commands.constructors.bukkit

import dev.cypdashuhn.rooster.commands.SimpleArgumentType
import dev.cypdashuhn.rooster.commands.UnfinishedArgument
import dev.cypdashuhn.rooster.commands.constructors.ListArgument
import dev.cypdashuhn.rooster.commands.playerMessage
import dev.cypdashuhn.rooster.commands.playerMessageExtra
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlayerArgument {
    fun single(
        key: String = "player",
        notMatchingMessage: String = "rooster.player.not_matching_error",
        playerPlaceholder: String = "player",
        onMissingKey: String = "rooster.player.missing_error",
    ): UnfinishedArgument {
        return ListArgument.single(
            key = key,
            list = Bukkit.getOnlinePlayers().map { it.name },
            notMatchingError = playerMessageExtra(notMatchingMessage, playerPlaceholder),
            onMissing = playerMessage(onMissingKey),
        )
    }

    class PlayerArgumentType(argument: UnfinishedArgument, argKey: String) :
        SimpleArgumentType<Player>("Player", argument, argKey)

    fun multiple(
        key: String = "player",
        notMatchingMessage: String = "rooster.player.not_matching_error",
        playerPlaceholder: String = "player",
        onMissingKey: String = "rooster.player.missing_error",
    ): UnfinishedArgument {
        return ListArgument.chainable(
            key = key,
            list = Bukkit.getOnlinePlayers().map { it.name },
            notMatchingError = playerMessageExtra(notMatchingMessage, playerPlaceholder),
            onMissing = playerMessage(onMissingKey),
        )
    }

    class PlayerListArgumentType(argument: UnfinishedArgument, argKey: String) :
        SimpleArgumentType<List<Player>>("PlayerList", argument, argKey)
}