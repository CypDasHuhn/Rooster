package dev.cypdashuhn.rooster.generator

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import dev.cypdashuhn.rooster.core.PluginInfo
import dev.cypdashuhn.rooster.core.RoosterPlugin

class PluginYmlProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val pluginClass: KSClassDeclaration = resolver
            .getAllFiles()
            .map {
                it.declarations
                    .first {
                        it is KSClassDeclaration &&
                                it.superTypes.any { it.resolve().declaration.qualifiedName?.asString() == RoosterPlugin::class.qualifiedName }
                    } as KSClassDeclaration
            }
            .first()

        val pluginInfo = pluginClass.getAnnotationsByType(PluginInfo::class).firstOrNull()

        val pluginYml = buildString {
            appendLine("name: ${pluginInfo?.name ?: pluginClass.simpleName.asString()}")
            appendLine("version: ${pluginInfo?.version ?: "1.0.0"}")
            appendLine("main: ${pluginClass.qualifiedName!!.asString()}")
            appendLine("apiVersion: ${pluginInfo?.apiVersion ?: "1.21.4"}")

            fun addIfNotNull(key: String, value: String?) {
                if (value == null) return
                if (value.equals("[none]", ignoreCase = false)) return
                if (value == "![none]") appendLine("$key: [none]")
                appendLine("$key: $value")
            }
            addIfNotNull("description", pluginInfo?.description)
            addIfNotNull("author", pluginInfo?.author)
            addIfNotNull("website", pluginInfo?.website)
            addIfNotNull("prefix", pluginInfo?.prefix)

            appendLine("load: ${pluginInfo?.load?.toString() ?: "POSTWORLD"}")

            fun addList(key: String, value: Array<String>?) {
                if (value == null) return
                if (value.isEmpty()) return
                appendLine("$key:")
                value.forEach { appendLine("  - $it") }
            }
            addList("dependencies", pluginInfo?.authors)
            addList("contributors", pluginInfo?.contributors)
            addList("libraries", pluginInfo?.libraries)
            addList("depend", pluginInfo?.depend)
            addList("softdepend", pluginInfo?.softdepend)
            addList("loadbefore", pluginInfo?.loadbefore)
            addList("loadafter", pluginInfo?.loadbefore)
            addList("provides", pluginInfo?.provides)

            if (pluginInfo != null && pluginInfo.permissions.isNotEmpty()) {
                appendLine("permissions:")
                pluginInfo.permissions.forEach { permission ->
                    appendLine("    ${permission.name}: ")
                    appendLine("        description: ${permission.description}")
                    appendLine("        default: ${permission.default.bukkitName}")
                    appendLine("        children:")
                    permission.children.forEach { appendLine("            $it: true") }
                }
            }
        }

        environment.codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false),
            packageName = "",
            fileName = "plugin",
            extensionName = "yml"
        ).use { it.write(pluginYml.toByteArray()) }

        return emptyList()
    }
}

class PluginYmlProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return PluginYmlProcessor(environment)
    }
}