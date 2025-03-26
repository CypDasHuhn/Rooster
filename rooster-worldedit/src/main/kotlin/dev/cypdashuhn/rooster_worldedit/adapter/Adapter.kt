package dev.cypdashuhn.rooster_worldedit.adapter

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.regions.CuboidRegion
import dev.cypdashuhn.rooster.region.Region
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import com.sk89q.worldedit.regions.Region as WERegion

fun Region.toWorldEditRegion(): CuboidRegion {
    return CuboidRegion(
        BukkitAdapter.adapt(this.edge1.world),
        BukkitAdapter.asBlockVector(this.edge1),
        BukkitAdapter.asBlockVector(this.edge2)
    )
}

fun WERegion.toRegion(world: World): Region {
    return Region(this.minimumPoint.toLocation(world), this.maximumPoint.toLocation(world))
}

fun WERegion.toRegion(player: Player): Region {
    return Region(this.minimumPoint.toLocation(player.world), this.maximumPoint.toLocation(player.world))
}

fun BlockVector3.toLocation(world: World): Location {
    return Location(world, x().toDouble(), y().toDouble(), z().toDouble())
}

fun Player.worldEditSelection(): WERegion? {
    val actor = BukkitAdapter.adapt(this)
    val manager = WorldEdit.getInstance().sessionManager
    val localSession = manager.get(actor)

    val selectionWorld = localSession.selectionWorld ?: return null
    return localSession.getSelection(selectionWorld)
}