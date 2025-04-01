package dev.cypdashuhn.rooster.demo.ui

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.Slots
import dev.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object MenuInterface : PageInterface<MenuInterface.MenuContext>("", MenuContext::class) {
    class MenuContext(
        override var page: Int
    ) : PageContext(page)

    override fun initializePages(): List<Page<MenuContext>> = pages {
        page(1) {
            add(InterfaceItem(slots = Slots(5), itemStack = createItem(Material.GRAVEL), action = { }))
        }
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