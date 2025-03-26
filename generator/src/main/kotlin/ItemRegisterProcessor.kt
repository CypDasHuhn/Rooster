package dev.cypdashuhn.rooster.generator

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import dev.cypdashuhn.rooster.commands.RoosterCommand

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class RoosterIgnore

val targetTypes = listOf<String>(
    RoosterCommand::class.qualifiedName!!,
    //RoosterInterface::class.qualifiedName!!,
    //Listener::class.qualifiedName!!,
    //Table::class.qualifiedName!!,
)

data class TypeData(
    val typeName: String,
    val simpleTypeName: String,
    val foundInstances: List<String>
)

class ItemRegisterProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        //getSubclassesAndProperties(resolver, Listener::class.qualifiedName!!)

        val results = targetTypes.map { type ->
            /*val targetType = resolver.getClassDeclarationByName(type)?.asStarProjectedType()
                ?: return emptyList<KSAnnotated>().also { environment.logger.warn("Target class not found") }
*/
            val result = resolver.getAllFiles()
                .flatMap { file ->
                    file.declarations.flatMap { checkDeclaration(it, type) }
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

fun checkDeclaration(declaration: KSDeclaration, targetType: String): List<String> {
    if (declaration.annotations.any { it.annotationType.resolve().declaration.simpleName.asString() == RoosterIgnore::class.simpleName }) {
        return emptyList()
    }
    return when (declaration) {
        is KSClassDeclaration -> {
            if (declaration.classKind == ClassKind.OBJECT && declaration.superTypes.any { it.resolve().declaration.qualifiedName!!.toString() == targetType }) {
                listOf("${declaration.packageName.asString()}.${declaration.simpleName.asString()}")
            } else emptyList()
        }

        is KSPropertyDeclaration -> {
            if (declaration.type.resolve().declaration.qualifiedName.toString() == targetType) {
                listOf("${declaration.packageName.asString()}.${declaration.simpleName.asString()}")
            } else emptyList()
        }

        else -> emptyList()
    }
}

class ItemRegisterProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ItemRegisterProcessor(environment)
    }
}