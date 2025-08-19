package dev.cypdashuhn.rooster.demo

import be.seeseemelk.mockbukkit.MockBukkit
import dev.cypdashuhn.rooster.caching.InterfaceChachableLambda
import dev.cypdashuhn.rooster.caching.InterfaceDependency
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface

fun main() {
    val server = MockBukkit.mock()

    MockBukkit.load(DemoPlugin::class.java)

    val player = server.addPlayer()

    var context = PageInterface.PageContext(0)
    var slot = 0

    fun info() = InterfaceInfo(slot, context, player)

    val info1 = info()
    val cachedLambda =
        InterfaceChachableLambda({ slot }, InterfaceDependency.none<PageInterface.PageContext>().dependsOnSlot())

    val res1 = cachedLambda.get(info1)
    val res2 = cachedLambda.get(info1)

    println(res1)
    println(res2)
}
