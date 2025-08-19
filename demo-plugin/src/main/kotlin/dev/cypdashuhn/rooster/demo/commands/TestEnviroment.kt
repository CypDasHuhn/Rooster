package dev.cypdashuhn.rooster.demo.commands

import dev.cypdashuhn.rooster.commands.Argument
import dev.cypdashuhn.rooster.commands.Arguments
import dev.cypdashuhn.rooster.commands.RoosterCommand
import dev.cypdashuhn.rooster.commands.UnfinishedArgument
import dev.cypdashuhn.rooster.core.Rooster
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.entity.Player

object TestInterfacesCommand : RoosterCommand("testInterfaces") {
    const val INTERFACE_KEY = "interface"

    override fun content(arg: UnfinishedArgument): Argument {
        return arg.followedBy(
            Arguments.list.single(
                key = INTERFACE_KEY,
                list = Rooster.registered.interfaces.map { it.interfaceName },
                notMatchingError = { info, name -> info.sender.sendMessage("No such interface $name") },
                onMissing = { sender.sendMessage("Please specify an interface") },
                transformValue = { _, name -> Rooster.registered.interfaces.first { it.interfaceName == name } }
            ).onExecute {
                val targetInterface = context[INTERFACE_KEY] as RoosterInterface<Context>
                val context = targetInterface.getContext(sender as Player)
                targetInterface.openInventory(sender as Player, context)
            }
        )
    }
}