package dev.cypdashuhn.rooster.core

annotation class Attribute(val key: String, val value: String)
annotation class Permission(
    val name: String,
    val description: String,
    val default: PermissionType = PermissionType.OP,
    val children: Array<String> = []
)

enum class LoadType {
    POSTWORLD,
    STARTUP
}
enum class PermissionType(val bukkitName: String) {
    ALL("true"),
    NONE("false"),
    OP("op"),
    NOT_OP("not op")
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class PluginInfo(
    val name: String,
    val version: String = "1.0.0",
    /** The API version of the plugin. If not filled, the API version will be the newest version of Paper */
    val apiVersion: String = "1.21.4",

    val description: String = "[none]",

    val author: String = "[none]",
    val authors: Array<String> = [],
    val contributors: Array<String> = [],
    val website: String = "[none]",

    val load: LoadType = LoadType.POSTWORLD,
    val prefix: String = "[none]",

    val permissions: Array<Permission> = [],

    val libraries: Array<String> = [],
    val depend: Array<String> = [],
    val softdepend: Array<String> = [],
    val loadbefore: Array<String> = [],
    val provides: Array<String> = [],
)