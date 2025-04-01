package dev.cypdashuhn.rooster.demo.ui

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object MenuInterface : PageInterface<MenuInterface.MenuContext>("", MenuContext::class) {
    class MenuContext(
        override var page: Int
    ) : PageContext(page)

    override fun initializePages(): List<Page<MenuContext>> {
        return listOf(
            Page(
                1, listOf(

                )
            )
        )
    }

    override fun getInventoryName(player: Player, context: MenuContext): String {
        return "DemoInterface #${context.page}"
    }

    override fun defaultContext(player: Player): MenuContext {
        return MenuContext(0)
    }
}

class EmptyContext : Context()

object TestInterface : NoContextInterface("") {
    override fun getInventory(player: Player, context: EmptyContext): Inventory {
        TODO("Not yet implemented")
    }

    override fun getInterfaceItems(): List<InterfaceItem<EmptyContext>> {
        TODO("Not yet implemented")
    }
}