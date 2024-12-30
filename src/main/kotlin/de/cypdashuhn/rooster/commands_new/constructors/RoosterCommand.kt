package de.cypdashuhn.rooster.commands_new.constructors

import org.bukkit.command.CommandSender

abstract class RoosterCommand {
    val labels: List<String>
    val onStart: (CommandSender) -> Boolean
    val key: String

    constructor(
        labels: List<String>,
        onStart: (CommandSender) -> Boolean = { true },
        key: String = "label"
    ) {
        if (labels.isEmpty()) throw IllegalStateException("labels must not be empty")

        this.labels = labels
        this.onStart = onStart
        this.key = key
    }

    constructor(
        label: String,
        onStart: (CommandSender) -> Boolean = { true },
        key: String = "label"
    ) {
        this.labels = listOf(label)
        this.onStart = onStart
        this.key = key
    }

    internal fun command() by lazy { content(Arguments.literal.single(key)) }
    abstract fun content(arg: UnfinishedArgument): Argument
}