package de.cypdashuhn.rooster.ui.context

import de.cypdashuhn.rooster.core.RoosterService
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import org.bukkit.entity.Player
import kotlin.reflect.KClass

abstract class InterfaceContextProvider : RoosterService {
    /**
     * (Upsert) Saves a Context-Instance, with the keys player and interface
     * (recommended: PlayerUUID, InterfaceName)
     */
    abstract fun <T : Context> updateContext(player: Player, interfaceInstance: Interface<T>, context: T)

    /**
     * Reads the Context-Instance if it exists, with the keys player and
     * interface (recommended: PlayerUUID, InterfaceName)
     */
    abstract fun <T : Context> getContext(player: Player, interfaceInstance: Interface<T>): T?

    override fun targetClass(): KClass<out RoosterService> {
        return InterfaceContextProvider::class
    }
}