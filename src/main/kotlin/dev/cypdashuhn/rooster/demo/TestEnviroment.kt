package dev.cypdashuhn.rooster.demo

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

    override fun content(arg: UnfinishedArgument): dev.cypdashuhn.rooster.commands.Argument {
        return arg.followedBy(
            dev.cypdashuhn.rooster.commands.Arguments.list.single(
                key = INTERFACE_KEY,
                list = Rooster.registered.interfaces.map { it.interfaceName },
                notMatchingError = { info, name -> info.sender.sendMessage("No such interface $name") },
                onMissing = { info -> info.sender.sendMessage("Please specify an interface") },
                transformValue = { _, name -> Rooster.registered.interfaces.first { it.interfaceName == name } }
            ).onExecute {
                val targetInterface = it.context[INTERFACE_KEY] as RoosterInterface<Context>
                val context = targetInterface.getContext(it.sender as Player)
                targetInterface.openInventory(it.sender as Player, context)
            }
        )
    }
}