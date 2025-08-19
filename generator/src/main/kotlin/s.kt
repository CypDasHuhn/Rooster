package dev.cypdashuhn.rooster.generator

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KSTypesNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import kotlin.reflect.KClass

@OptIn(KspExperimental::class)
fun getSubclassesAndProperties(
    resolver: Resolver,
    targetSuperClass: String
): List<Pair<KSClassDeclaration, List<KSPropertyDeclaration>>> {
    val result = mutableListOf<Pair<KSClassDeclaration, List<KSPropertyDeclaration>>>()

    resolver.getAllFiles().flatMap { it.declarations }
        .filterIsInstance<KSClassDeclaration>()
        .forEach { classDecl ->
            try {
                val superTypes = classDecl.superTypes.map { it.resolve() }
                val matches = superTypes.any { superType ->
                    isMatchingSuperType(superType, targetSuperClass)
                }

                if (matches) {
                    val properties = classDecl.getAllProperties().toList()
                    result.add(classDecl to properties)
                }
            } catch (e: KSTypesNotPresentException) {
                val unresolvedTypeNames = e.ksTypes.map { it.declaration.qualifiedName?.asString() }
                if (targetSuperClass in unresolvedTypeNames) {
                    val properties = classDecl.getAllProperties().toList()
                    result.add(classDecl to properties)
                }
            }
        }

    return result
}

@OptIn(KspExperimental::class)
fun parseAnnotationClassParameter(block: () -> KClass<*>): String? {
    return try { // KSTypeNotPresentException will be thrown
        block.invoke().qualifiedName
    } catch (e: KSTypeNotPresentException) {
        var res: String? = null
        val declaration = e.ksType.declaration
        if (declaration is KSClassDeclaration) {
            declaration.qualifiedName?.asString()?.let {
                res = it
            }
        }
        res
    }
}

@OptIn(KspExperimental::class)
private fun isMatchingSuperType(type: KSType, targetSuperClass: String): Boolean {
    return try {
        // Resolve the supertype's qualified name
        type.declaration.qualifiedName?.asString() == targetSuperClass
    } catch (e: KSTypesNotPresentException) {
        // Fallback to checking the unresolved name
        e.ksTypes.map { it.declaration.qualifiedName?.asString() }.any { it == targetSuperClass }
    }
}