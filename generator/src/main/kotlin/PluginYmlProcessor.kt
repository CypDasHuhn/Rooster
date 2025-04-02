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