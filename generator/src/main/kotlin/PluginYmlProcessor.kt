package dev.cypdashuhn.rooster.generator

import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class PluginYmlProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val roosterPluginType = resolver.getClassDeclarationByName("dev.cypdashuhn.rooster.core.RoosterPlugin")
            ?: return emptyList<KSAnnotated>().also { environment.logger.warn("Target class not found") }

        val plugin = resolver.getAllFiles()
            .flatMap { file ->
                file.declarations.flatMap {
                    if (it is KSClassDeclaration && it.superTypes.any { it.resolve() == roosterPluginType }) {
                        listOf(it)
                    } else listOf()
                }
            }.first()


        val results = targetTypes.map { type ->
            val targetType = resolver.getClassDeclarationByName(type)?.asStarProjectedType()
                ?: return emptyList<KSAnnotated>().also { environment.logger.warn("Target class not found") }

            val result = resolver.getAllFiles()
                .flatMap { file ->
                    file.declarations.flatMap { checkDeclaration(it, targetType.toString()) }
                }
                .toList()

            TypeData(type, type.split(".").last(), result)
        }

        val file = environment.codeGenerator.createNewFile(
            dependencies = Dependencies(aggregating = false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "dev.cypdashuhn.rooster.generated",
            fileName = "RegisteredItems",
        )

        file.bufferedWriter().use { writer ->
            writer.write("package dev.cypdashuhn.rooster.generated \n")
            for (typeData in results) {
                if (typeData.foundInstances.isEmpty()) continue
                writer.write(
                    """

val registered${typeData.simpleTypeName}s = listOf<${typeData.typeName}>(
    ${typeData.foundInstances.joinToString(",\n    ")}
)

""".trimIndent()
                )
            }
        }

        return emptyList()
    }
}

fun checkDeclarationa(declaration: KSDeclaration, targetType: KSType): List<String> {
    if (declaration.annotations.any { it.annotationType.resolve().declaration.simpleName.asString() == RoosterIgnore::class.simpleName }) {
        return emptyList()
    }
    return when (declaration) {
        is KSClassDeclaration -> {
            if (declaration.classKind == ClassKind.OBJECT && declaration.superTypes.any { it.resolve() == targetType }) {
                listOf("${declaration.packageName.asString()}.${declaration.simpleName.asString()}")
            } else emptyList()
        }

        is KSPropertyDeclaration -> {
            if (declaration.type.resolve() == targetType) {
                listOf("${declaration.packageName.asString()}.${declaration.simpleName.asString()}")
            } else emptyList()
        }

        else -> emptyList()
    }
}

class PluginYmlProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return PluginYmlProcessor(environment)
    }
}