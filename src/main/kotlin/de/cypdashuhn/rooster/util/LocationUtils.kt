package de.cypdashuhn.rooster.util

import org.bukkit.Axis
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.joml.Vector3d

fun Location.toVector3d(): Vector3d {
    return Vector3d(this.x, this.y, this.z)
}

fun Vector3d.toLocation(world: World, yaw: Float = 0f, pitch: Float = 0f): Location {
    return Location(world, this.x, this.y, this.z, yaw, pitch)
}

fun Location.value(axis: Axis): Double {
    return when (axis) {
        Axis.X -> this.x
        Axis.Y -> this.y
        Axis.Z -> this.z
    }
}

fun sameAxis(vararg axis: Axis): Boolean {
    val firstAxis = axis.first()
    axis.forEach {
        if (it != firstAxis) return false
    }
    return true
}

fun List<Location>.nearest(location: Location): Location {
    return this.map { Pair(it, it.distance(location)) }.minByOrNull { it.second }!!.first
}

fun List<Player>.nearest(location: Location): Player {
    return this.map { Pair(it, it.location.distance(location)) }.minByOrNull { it.second }!!.first
}