package de.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content

import de.cypdashuhn.rooster.listeners.ClickState
import de.cypdashuhn.rooster.listeners.hasClicks
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Slot
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.ui.items.constructors.ContextModifierItem
import de.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.reflect.KClass

/** Interface not finished, don't use! */
abstract class GraphInterface<ContextType : GraphInterface.GraphContext, DataType : Any>(
    override val interfaceName: String,
    override val contextClass: KClass<ContextType>,
    override val contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>> = (0 to 0) to (8 to 4)
) : IndexedContentInterface<ContextType, Pair<Int, Int>, DataType>(interfaceName, contextClass, contentArea) {
    abstract class GraphContext(
        var position: Pair<Int, Int> = 0 to 0
    ) : Context()

    private fun verticalPager() = ContextModifierItem<ContextType>(
        slot = bottomRow + 7,
        itemStack = createItem(Material.COMPASS),
        contextModifier = { clickInfo ->
            clickInfo.context.also { context ->
                val y = context.position.second
                var scrollAmount = if (clickInfo.event.hasClicks(ClickState.SHIFT_CLICK)) 5 else 1
                if (clickInfo.event.hasClicks(ClickState.LEFT_CLICK)) scrollAmount *= -1
                context.position = context.position.first to y + scrollAmount
            }
        }
    )
    open fun verticalPagerModifier(item: InterfaceItem<ContextType>) = item

    private fun horizontalPager() = ContextModifierItem<ContextType>(
        slot = bottomRow + 8,
        itemStack = createItem(Material.COMPASS),
        contextModifier = { clickInfo ->
            clickInfo.context.also { context ->
                val x = context.position.first
                var scrollAmount = if (clickInfo.event.hasClicks(ClickState.SHIFT_CLICK)) 5 else 1
                if (clickInfo.event.hasClicks(ClickState.LEFT_CLICK)) scrollAmount *= -1
                context.position = x + scrollAmount to context.position.second
            }
        }
    )
    open fun horizontalPagerModifier(item: InterfaceItem<ContextType>) = item

    final override fun getFrameItems(): List<InterfaceItem<ContextType>> = listOf(
        verticalPagerModifier(verticalPager()),
        horizontalPagerModifier(horizontalPager())
    )

    abstract override fun defaultContext(player: Player): ContextType

    override fun slotToId(slot: Slot, context: ContextType, player: Player): Pair<Int, Int>? {
        val (x, y) = offset(slot) ?: return null
        val (posX, posY) = context.position

        return x + posX to y + posY
    }
}