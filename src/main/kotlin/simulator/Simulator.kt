package de.cypdashuhn.rooster.simulator

import be.seeseemelk.mockbukkit.MockBukkit
import de.cypdashuhn.rooster.commands.ArgumentParser
import org.bukkit.Bukkit

object Simulator {

    fun startSimulator() {
        isSimulating = true;

        val server = MockBukkit.mock()
        val plugin = MockBukkit.createMockPlugin()
        Rooster.initialize(plugin)

        var player = server.addPlayer()

        println("Welcome to the Input Simulator. Type commands to simulate input. Type 'exit' to quit.")

        while (true) {
            print("> ")
            val input = readlnOrNull()?.trim() ?: continue

            if (input.equals("exit", ignoreCase = true)) {
                println("Exiting the simulator.")
                break
            }

            val command = input.split(" ").firstOrNull()
            val args = input.substring(command?.length ?: 0) ?: ""

            when (command) {
                "complete" -> {
                    CommandSimulator.commandComplete(args, player)
                }
                "invoke" -> {
                    CommandSimulator.commandInvoke(args, player)
                }
                else -> println("Unknown command: $input")
            }
        }
    }

    private var isSimulating = false
    fun nonTest(block: () -> Unit) {
        if (!isSimulating) {
            block()
        }
    }
    fun onlyTest(block: () -> Unit) {
        if (isSimulating) {
            block()
        }
    }

}