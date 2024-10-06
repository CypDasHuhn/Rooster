package de.cypdashuhn.rooster.ui.items.constructors

import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.inventory.ItemStack

open class ContextModifierItem<T : Context> : InterfaceItem<T> {
    protected companion object {
        fun <T : Context> contextModifierAction(
            contextModifier: (ClickInfo<T>) -> T,
            furtherAction: (ClickInfo<T>) -> Unit
        ): (ClickInfo<T>) -> Unit {
            return {
                furtherAction(it)
                val context = contextModifier(it)
                it.clickedInterface.openInventory(it.click.player, context)
            }
        }
    }

    fun changeContextModifierAction(
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { }
    ): ContextModifierItem<T> {
        return ContextModifierItem(
            this.conditionMap,
            this.itemStackCreator,
            contextModifier,
            furtherAction,
            this.priority
        )
    }

    constructor(
        conditionMap: Map<String, (InterfaceInfo<T>) -> Boolean>,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(conditionMap, itemStackCreator, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(condition, itemStackCreator, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(condition, itemStack, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(slot, condition, itemStackCreator, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(slot, condition, itemStack, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        slots: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(slots, condition, itemStackCreator, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        slots: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(slots, condition, itemStack, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        slots: List<Int>,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(slots, condition, itemStackCreator, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        slots: List<Int>,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        contextModifier: (ClickInfo<T>) -> T, furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(slots, condition, itemStack, contextModifierAction(contextModifier, furtherAction), priority)
}