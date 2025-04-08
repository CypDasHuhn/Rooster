package dev.cypdashuhn.rooster.simulator

import dev.cypdashuhn.rooster.core.Rooster
import dev.cypdashuhn.rooster.core.RoosterShell
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class RoosterSimulator(val pluginName: String = "demo") : RoosterShell {
    init {
        Rooster.initServices()
    }

    fun startTerminal() {
        Simulator.initializeSimulator(this)

        Simulator.startTerminal()
    }

    fun simulate(preserveFolder: Boolean = false, actionBlock: (Player) -> Unit) {
        val player = Simulator.initializeSimulator(this)

        actionBlock(player)
        if (!preserveFolder) Simulator.deleteMockDirectory()
    }

    final override fun initializeRooster(
        plugin: JavaPlugin
    ) {
        super.initializeRooster(plugin)
        val directoryPath = Rooster.plugin.dataFolder.absolutePath
        val clickablePath =
            "file:///$directoryPath".replace("\\", "/")
        println("Current Mock Directory: $clickablePath")
    }

}