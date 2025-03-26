package dev.cypdashuhn.rooster.demo.other

import dev.cypdashuhn.rooster.listeners.usable_item.ItemEffect
import dev.cypdashuhn.rooster.listeners.usable_item.UsableItem
import dev.cypdashuhn.rooster.listeners.usable_item.hasClicks
import dev.cypdashuhn.rooster.localization.tSend
import dev.cypdashuhn.rooster.util.ClickType
import dev.cypdashuhn.rooster.util.createItem
import dev.cypdashuhn.rooster.util.giveItem
import dev.cypdashuhn.rooster_demo.ui.MenuInterface
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player

val compass = UsableItem(
    itemStack = createItem(Material.COMPASS, name = Component.text("Menu Compass")),
    ItemEffect(
        { it.hasClicks(ClickType.LEFT_CLICK) },
        { MenuInterface.openInventory(it.player, MenuInterface.getContext(it.player)) }),
    ItemEffect(
        { it.hasClicks(ClickType.RIGHT_CLICK) },
        ItemEffect(
            { it.hasClicks(ClickType.SHIFT_CLICK) },
            { it.player.tSend("rooster.compass.shift_click") }
        ),
        ItemEffect(
            { !it.hasClicks(ClickType.SHIFT_CLICK) },
            { it.player.tSend("rooster.compass.normal_click") }
        )
    )
)

fun Player.giveMenuCompass() {
    giveItem(compass.item)
}