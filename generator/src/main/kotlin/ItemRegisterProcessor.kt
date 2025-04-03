package dev.cypdashuhn.rooster.generator

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import dev.cypdashuhn.rooster.commands.RoosterCommand
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.event.Listener
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class RoosterIgnore

val targetTypes = listOf<String>(
    RoosterCommand::class.qualifiedName!!,
    RoosterInterface::class.qualifiedName!!,
    Listener::class.qualifiedName!!,
    Table::class.qualifiedName!!,
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

    fun isTypeOf(declaration: KSClassDeclaration): Boolean {
        return declaration.qualifiedName?.asString() == targetType
                || declaration.superTypes.any { it.resolve().declaration is KSClassDeclaration && isTypeOf(it.resolve().declaration as KSClassDeclaration) }
    }

    fun printDeclaration(declaration: KSDeclaration) {
        logger.warn(declaration.qualifiedName?.asString() ?: "no-name")
        if (declaration is KSClassDeclaration) {
            declaration.superTypes.forEach { printDeclaration(it.resolve().declaration) }
        } else if (declaration is KSPropertyDeclaration) {
            printDeclaration(declaration.type.resolve().declaration)
        }
    }
    printDeclaration(declaration)
    logger.warn("---")

    return when (declaration) {
        is KSClassDeclaration -> {
            if (declaration.classKind == ClassKind.OBJECT && isTypeOf(declaration)) {
                listOf("${declaration.packageName.asString()}.${declaration.simpleName.asString()}")
            } else emptyList()
        }

        is KSPropertyDeclaration -> {
            if (declaration.type.resolve().declaration is KSClassDeclaration && isTypeOf(declaration.type.resolve().declaration as KSClassDeclaration)) {
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