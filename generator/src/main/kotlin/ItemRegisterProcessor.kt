package dev.cypdashuhn.rooster.generator

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import dev.cypdashuhn.rooster.commands.RoosterCommand
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class RoosterIgnore

val targetTypes = listOf<String>(
    RoosterCommand::class.qualifiedName!!,
    RoosterInterface::class.qualifiedName!!,
    "org.bukkit.entity.Listener",
    "dev.cypdashuhn.rooster.listener.RoosterListener",
    Table::class.qualifiedName!!,
    IntIdTable::class.qualifiedName!!
)

data class TypeData(
    val typeName: String,
    val simpleTypeName: String,
    val foundInstances: List<String>
)

class ItemRegisterProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val results = targetTypes.map { type ->
            val result = resolver.getAllFiles()
                .flatMap { file ->
                    file.declarations.flatMap { checkDeclaration(it, type, logger = environment.logger) }
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

val registered${typeData.simpleTypeName}s = listOf(
    ${typeData.foundInstances.joinToString(",\n    ")}
)

""".trimIndent()
                )
            }
        }

        return emptyList()
    }
}

fun checkDeclaration(declaration: KSDeclaration, targetType: String, logger: KSPLogger): List<String> {
    if (declaration.annotations.any { it.annotationType.resolve().declaration.simpleName.asString() == RoosterIgnore::class.simpleName }) {
        return emptyList()
    }

    fun isTypeOf(declaration: KSDeclaration): Boolean {
        return declaration.qualifiedName!!.asString() == targetType
                || (declaration.parentDeclaration != null && isTypeOf(declaration.parentDeclaration!!))
    }

    return when (declaration) {
        is KSClassDeclaration -> {
            if (declaration.classKind == ClassKind.OBJECT) {
                logger.warn(declaration.qualifiedName!!.asString())
                fun logSubType(declaration: KSDeclaration) {
                    logger.warn(declaration.qualifiedName!!.asString())
                    logger.warn(declaration.parent?.origin?.name ?: "no parent node")
                    if (declaration.parentDeclaration == null) logger.warn("final declaration")
                    if (declaration.parentDeclaration != null) logSubType(declaration.parentDeclaration!!)
                }
                declaration.superTypes.forEach {
                    logSubType(it.resolve().declaration)
                }
                logger.warn("---")
            }

            if (declaration.classKind == ClassKind.OBJECT && declaration.superTypes.any { isTypeOf(it.resolve().declaration) }) {
                listOf("${declaration.packageName.asString()}.${declaration.simpleName.asString()}")
            } else emptyList()
        }

        is KSPropertyDeclaration -> {
            if (isTypeOf(declaration.type.resolve().declaration)) {
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