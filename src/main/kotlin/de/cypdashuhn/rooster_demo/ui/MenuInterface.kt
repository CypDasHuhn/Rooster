package de.cypdashuhn.rooster_demo.ui

import de.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import org.bukkit.entity.Player

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