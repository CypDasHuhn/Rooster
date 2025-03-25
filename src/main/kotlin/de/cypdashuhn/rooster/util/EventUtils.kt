package de.cypdashuhn.rooster.util

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

enum class MouseClickType {
    LEFT,
    MIDDLE,
    RIGHT
}

enum class ClickType(
    val isShift: Boolean?,
    val mouseClickType: MouseClickType?
) {
    SHIFT_CLICK(true, null),
    NORMAL_CLICK(false, null),

    LEFT_CLICK(null, MouseClickType.LEFT),
    MIDDLE_CLICK(null, MouseClickType.MIDDLE),
    RIGHT_CLICK(null, MouseClickType.RIGHT),

    LEFT_SHIFT_CLICK(true, MouseClickType.LEFT),
    MIDDLE_SHIFT_CLICK(true, MouseClickType.MIDDLE),
    RIGHT_SHIFT_CLICK(true, MouseClickType.RIGHT),

    LEFT_NORMAL_CLICK(false, MouseClickType.LEFT),
    MIDDLE_NORMAL_CLICK(false, MouseClickType.MIDDLE),
    RIGHT_NORMAL_CLICK(false, MouseClickType.RIGHT)
}

infix fun InventoryClickEvent.typeOf(clickType: ClickType): Boolean {
    return typeOf(clickType, allTrue = true)
}

fun InventoryClickEvent.typeOf(vararg clickTypes: ClickType, allTrue: Boolean): Boolean {
    val condition = { it: ClickType ->
        it.isShift == null || it.isShift == this.isShiftClick &&
                it.isLeft == null || it.isLeft == this.isLeftClick
    }
    return if (allTrue) clickTypes.all(condition) else clickTypes.any(condition)
}

infix fun InventoryClickEvent.typeOf(clickTypes: List<ClickType>): Boolean {
    return typeOf(clickTypes, allTrue = true)
}

infix fun PlayerInteractEvent.typeOf(clickType: ClickType) {

}

fun InventoryClickEvent.typeOf(vararg clickTypes: ClickType, allTrue: Boolean): Boolean {
    val condition = { it: ClickType ->
        it.isShift == null || it.isShift == this.isShiftClick &&
                it.isLeft == null || it.isLeft == this.isLeftClick
    }
    return if (allTrue) clickTypes.all(condition) else clickTypes.any(condition)
}

infix fun InventoryClickEvent.typeOf(clickTypes: List<ClickType>): Boolean {
    return typeOf(clickTypes, allTrue = true)
}

fun example() {
    var event = InventoryClickEvent(null, null, null, null, null)

    when {
        event typeOf ClickType.LEFT_CLICK -> {}
        event typeOf ClickType.RIGHT_CLICK -> {}
    }
}