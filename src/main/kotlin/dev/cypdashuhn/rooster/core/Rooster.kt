package dev.cypdashuhn.rooster.core

import com.google.common.cache.CacheBuilder
import dev.cypdashuhn.rooster.commands.RoosterCommand
import dev.cypdashuhn.rooster.commands.parsing.Command
import dev.cypdashuhn.rooster.commands.parsing.Completer
import dev.cypdashuhn.rooster.core.config.RoosterOptions
import dev.cypdashuhn.rooster.database.initDatabase
import dev.cypdashuhn.rooster.localization.provider.LocaleProvider
import dev.cypdashuhn.rooster.localization.provider.SqlLocaleProvider
import dev.cypdashuhn.rooster.ui.context.InterfaceContextProvider
import dev.cypdashuhn.rooster.ui.context.SqlInterfaceContextProvider
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Table
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object Rooster {
    lateinit var plugin: JavaPlugin
    internal val roosterLogger: Logger = Logger.getLogger("Rooster")
    val logger: Logger
        get() {
            if (!::plugin.isInitialized) {
                throw IllegalStateException("Plugin is not initialized. Do not use the Logger before Rooster is initialized.")
            }
            return plugin.logger
        }

    fun runTask(task: () -> Unit) {
        if (!::plugin.isInitialized) {
            throw IllegalStateException("Plugin is not initialized. Do not run tasks here before Rooster is initialized.")
        }
        Bukkit.getScheduler().runTask(plugin, Runnable { task() })
    }

    lateinit var pluginInfo: PluginInfo

    val services = RoosterServices
    val options = RoosterOptions

    var databasePath: String? = null
    val pluginFolder: String by lazy { plugin.dataFolder.absolutePath }
    val roosterFolder: String by lazy { plugin.dataFolder.parentFile.resolve("Rooster").absolutePath }

    object registered {
        val commands: MutableList<RoosterCommand> = mutableListOf()
        val interfaces: MutableList<RoosterInterface<*>> = mutableListOf()
        val tables: MutableList<Table> = mutableListOf()
        val listeners: MutableList<Listener> = mutableListOf()
        val functions: MutableMap<String, Method> = mutableMapOf()
    }

    val cache = dev.cypdashuhn.rooster.RoosterCache<String, Any>(
        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
    )

    var dynamicTables = mutableListOf<Table>()

    internal val localeProvider by RoosterServices.delegate<LocaleProvider>()
    internal val interfaceContextProvider by RoosterServices.delegate<InterfaceContextProvider>()

    fun initServices() {
        RoosterServices.set<LocaleProvider>(SqlLocaleProvider(mapOf("en_US" to Locale.ENGLISH), "en_US"))
        RoosterServices.set<InterfaceContextProvider>(SqlInterfaceContextProvider())
    }

    fun initialize(
        plugin: JavaPlugin,
        pluginInfo: PluginInfo,
    ) {
        Rooster.plugin = plugin
        Rooster.pluginInfo = pluginInfo

        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }

        initDatabase(registered.tables)

        // listeners
        val pluginManager = Bukkit.getPluginManager()
        for (listener in registered.listeners) {
            pluginManager.registerEvents(listener, plugin)
        }

        // commands
        registered.commands.flatMap { it.labels }.forEach { label ->
            plugin.getCommand(label)?.let {
                it.setExecutor(Command)
                it.tabCompleter = Completer
            }
        }
    }
}
