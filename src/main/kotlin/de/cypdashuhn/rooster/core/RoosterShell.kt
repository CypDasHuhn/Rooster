package de.cypdashuhn.rooster.core

import org.bukkit.plugin.java.JavaPlugin

interface RoosterShell {
    /**
     * Called before Rooster initializes. This is where you modify Rooster's
     * core variables.
     *
     * This hook gives you early access to adjust settings before Rooster runs
     * its setup process. Read more about it here: TODO: Add Doc Link
     */
    fun beforeInitialize() {}

    /**
     * Called after Rooster has fully initialized. Ideal for database access or
     * setup logic.
     *
     * You can safely assume all of Rooster's services are available when this
     * is called. Read more about it here: TODO: Add Doc Link
     */
    fun onInitialize() {}


    fun initializeRooster(
        plugin: JavaPlugin,
        pluginName: String,
        version: String = "1.0.0",
        apiVersion: String = "1.21.4"
    ) {
        beforeInitialize()
        Rooster.initialize(
            plugin = plugin,
            pluginInfo = PluginInfo(pluginName, version, apiVersion),
        )
        onInitialize()
    }
}