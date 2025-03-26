package dev.cypdashuhn.rooster.listeners.usable_item

import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

private fun eventHasClicks(clickStates: Array<out ClickType>, isLeft: Boolean, isShift: Boolean): Boolean {
    return clickStates.any {
        it.isShift == null || it.isShift == isShift &&
                it.isLeft == null || it.isLeft == isLeft
    }
}

fun PlayerInteractEvent.hasClicks(vararg clickStates: ClickState): Boolean {
    return eventHasClicks(clickStates, this.action.isLeftClick, this.player.isSneaking)
}

fun InventoryClickEvent.hasClicks(vararg clickStates: ClickState): Boolean {
    return eventHasClicks(clickStates, this.isLeftClick, this.isShiftClick)
}

class UsableItem() {
    lateinit var condition: (PlayerInteractEvent) -> Boolean
    lateinit var clickEffect: (PlayerInteractEvent) -> Unit
    private var itemGenerator: (() -> ItemStack)? = null
    val item
        get() = itemGenerator!!()
    lateinit var subEffects: List<ItemEffect>

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        clickEffect: (PlayerInteractEvent) -> Unit,
        itemGenerator: (() -> ItemStack),
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = clickEffect
        this.itemGenerator = itemGenerator
        this.subEffects = subEffects.toList()
    }

    constructor(
        itemStack: ItemStack,
        clickEffect: (PlayerInteractEvent) -> Unit,
        vararg subEffects: ItemEffect
    ) : this() {
        condition = { event -> event.item == itemStack }
        itemGenerator = { itemStack }
        this.clickEffect = clickEffect
        this.subEffects = subEffects.toList()
    }

    constructor(
        itemStack: ItemStack,
        vararg subEffects: ItemEffect
    ) : this() {
        condition = { event -> event.item == itemStack }
        itemGenerator = { itemStack }
        this.clickEffect = {}
        this.subEffects = subEffects.toList()
    }
}

class ItemEffect() {
    lateinit var condition: (PlayerInteractEvent) -> Boolean
    lateinit var clickEffect: (PlayerInteractEvent) -> Unit
    lateinit var subEffects: List<ItemEffect>

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        clickEffect: (PlayerInteractEvent) -> Unit,
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = clickEffect
        this.subEffects = subEffects.toList()
    }

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = { }
        this.subEffects = subEffects.toList()
    }
}