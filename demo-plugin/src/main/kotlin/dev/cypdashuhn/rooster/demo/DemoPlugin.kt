package dev.cypdashuhn.rooster.demo

import dev.cypdashuhn.rooster.core.Attribute
import dev.cypdashuhn.rooster.core.PluginInfo
import dev.cypdashuhn.rooster.core.RoosterPlugin

@PluginInfo(
    "DemoPlugin", "1.0.0", properties = [
        Attribute("author", "Cypdashuhn"),
    ]
)
class DemoPlugin : RoosterPlugin()