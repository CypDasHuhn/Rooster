package dev.cypdashuhn.rooster.core

annotation class Attribute(val key: String, val value: String)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class PluginInfo(
    val name: String,
    val version: String = "1.0.0",
    /** The API version of the plugin. If not filled, the API version will be the newest version of Paper */
    val apiVersion: String = "",
    val properties: Array<Attribute> = [],
)