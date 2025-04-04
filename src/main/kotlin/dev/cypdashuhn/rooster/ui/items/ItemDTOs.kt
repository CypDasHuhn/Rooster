package dev.cypdashuhn.rooster.ui.items

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.Slot
import dev.cypdashuhn.rooster.util.createItem
import dev.cypdashuhn.rooster.util.infix_gate.and
import dev.cypdashuhn.rooster.util.nextName
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ConditionMap<T : Context> {
    private var conditionMap: MutableMap<String, InterfaceInfo<T>.() -> Boolean> = mutableMapOf()

    fun add(condition: InterfaceInfo<T>.() -> Boolean, key: String = "anonymous") {
        conditionMap[nextName(key, conditionMap.keys.toList())] = condition
    }
    fun set(condition: InterfaceInfo<T>.() -> Boolean, key: String = "anonymous") {
        conditionMap[key] = condition
    }

    fun flatten(): InterfaceInfo<T>.() -> Boolean = { conditionMap.values.all { it(this) } }
}

class Condition<T : Context> {
    var condition: (InterfaceInfo<T>) -> Boolean = { true }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean
    ) {
        this.condition = condition
    }

    constructor(int: Slot, condition: (InterfaceInfo<T>) -> Boolean = { true }) {
        this.condition = { it: InterfaceInfo<T> -> it.slot == int && condition(it) } and condition
    }

    constructor(int: IntRange, condition: (InterfaceInfo<T>) -> Boolean = { true }) {
        this.condition = { it: InterfaceInfo<T> -> it.slot in int && condition(it) } and condition
    }

    operator fun invoke(): (InterfaceInfo<T>) -> Boolean {
        return condition
    }
}

class ItemStackCreator<T : Context> {
    var itemStackCreator: (InterfaceInfo<T>) -> ItemStack

    constructor(itemStackCreator: (InterfaceInfo<T>) -> ItemStack) {
        this.itemStackCreator = itemStackCreator
    }

    constructor(itemStack: ItemStack) {
        this.itemStackCreator = { itemStack }
    }

    constructor(material: Material, name: String? = null) {
        this.itemStackCreator = { createItem(material, name?.let { Component.text(it) }) }
    }

    operator fun invoke(): (InterfaceInfo<T>) -> ItemStack {
        return itemStackCreator
    }
}