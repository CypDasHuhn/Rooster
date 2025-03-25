package de.cypdashuhn.rooster_demo.other

import de.cypdashuhn.rooster.listeners.usable_item.ClickState
import de.cypdashuhn.rooster.listeners.usable_item.ItemEffect
import de.cypdashuhn.rooster.listeners.usable_item.UsableItem
import de.cypdashuhn.rooster.listeners.usable_item.hasClicks
import de.cypdashuhn.rooster.localization.tSend
import de.cypdashuhn.rooster.util.createItem
import de.cypdashuhn.rooster.util.giveItem
import de.cypdashuhn.rooster_demo.ui.MenuInterface
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

val compass = UsableItem(
    itemStack = createItem(Material.COMPASS, name = Component.text("Menu Compass")),
    ItemEffect(
        { it.hasClicks(ClickState.LEFT_CLICK) },
        { MenuInterface.openInventory(it.player, MenuInterface.getContext(it.player)) }),
    ItemEffect(
        { it.hasClicks(ClickState.RIGHT_CLICK) },
        ItemEffect(
            { it.hasClicks(ClickState.SHIFT_CLICK) },
            { it.player.tSend("rooster.compass.shift_click") }
        ),
        ItemEffect(
            { !it.hasClicks(ClickState.SHIFT_CLICK) },
            { it.player.tSend("rooster.compass.normal_click") }
        )
    )
)

fun Player.giveMenuCompass() {
    giveItem(compass.item)
}