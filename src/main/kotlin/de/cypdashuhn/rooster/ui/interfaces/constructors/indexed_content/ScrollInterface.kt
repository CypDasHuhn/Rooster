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

abstract class ScrollInterface<ContextType : ScrollInterface.ScrollContext, DataType : Any>(
    override val interfaceName: String,
    override val contextClass: KClass<ContextType>,
    open val scrollDirection: ScrollDirection = ScrollDirection.TOP_BOTTOM,
    override val contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>> = (0 to 0) to (8 to 4)
) : IndexedContentInterface<ContextType, Int, DataType>(interfaceName, contextClass, contentArea) {
    enum class ScrollDirection {
        TOP_BOTTOM,
        LEFT_RIGHT
    }

    abstract class ScrollContext(
        open var position: Int = 0
    ) : Context()

    private val rowSize: Int
        get() = if (scrollDirection == ScrollDirection.LEFT_RIGHT) contentXWidth else contentYWidth


    private fun scroller() = ContextModifierItem<ContextType>(
        slot = bottomRow + 8,
        itemStack = createItem(Material.COMPASS),
        contextModifier = { clickInfo ->
            clickInfo.context.also { context ->
                var scrollAmount = if (clickInfo.event.hasClicks(ClickState.SHIFT_CLICK)) 5 else 1
                if (clickInfo.event.hasClicks(ClickState.LEFT_CLICK)) scrollAmount *= -1

                context.position += scrollAmount
                if (context.position < 0) context.position = 0
            }
        }
    )
    open fun modifiedScroller(item: ContextModifierItem<ContextType>): ContextModifierItem<ContextType> = item

    final override fun getFrameItems(): List<InterfaceItem<ContextType>> = listOf(
        modifiedScroller(scroller())
    )

    final override fun slotToId(slot: Slot, context: ContextType, player: Player): Int? {
        val (x, y) = offset(slot) ?: return null
        val result = if (scrollDirection == ScrollDirection.LEFT_RIGHT)
            x + (y + context.position) * contentXWidth
        else y + (x + context.position) * contentYWidth

        return result
    }
}