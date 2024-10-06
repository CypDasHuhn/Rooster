package de.cypdashuhn.rooster.ui.interfaces.constructors.confirmation

import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass


abstract class BaseConfirmationInterface<T : Context>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    open val onConfirm: (ClickInfo<T>) -> Unit,
    open val onCancel: (CancelInfo<T>) -> Unit
) : Interface<T>(interfaceName, contextClass) {
    fun confirmationItem() = InterfaceItem<T>(
        slot = 8,
        itemStackCreator = { ItemStack(Material.GREEN_STAINED_GLASS_PANE) },
        action = onConfirm
    )

    fun modifyConfirmationItem(item: InterfaceItem<T>) = item
    fun cancelItem() = InterfaceItem<T>(
        slot = 0,
        itemStackCreator = { ItemStack(Material.RED_STAINED_GLASS_PANE) },
        action = onConfirm
    )

    fun modifyCancelItem(item: InterfaceItem<T>) = item

    abstract fun getOtherItems(): List<InterfaceItem<T>>
    override fun getInterfaceItems(): List<InterfaceItem<T>> {
        val list = mutableListOf(
            modifyConfirmationItem(confirmationItem()),
            modifyCancelItem(cancelItem())
        )

        list.addAll(getOtherItems())

        return list
    }

    override fun onClose(player: Player, context: T, event: InventoryCloseEvent) {
        onCancel(CancelInfo(CancelEvent(event), this, context))
    }
}