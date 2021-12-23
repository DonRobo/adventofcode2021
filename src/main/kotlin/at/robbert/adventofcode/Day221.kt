package at.robbert.adventofcode

import kotlin.math.max
import kotlin.math.min

sealed interface Volume {
    val volume: Long

    fun intersectionWith(other: Volume): Volume?
    fun intersects(other: Volume): Boolean = intersectionWith(other) != null

    operator fun minus(other: Volume): Volume
}

class CombinedVolume(
    val baseVolume: Cube,
    subtract: List<Cube>
) : Volume {

    val subtract: List<Cube> = subtract.mapNotNull { baseVolume.intersectionWith(it) }

    override val volume: Long = (baseVolume.volume - this.subtract.calculateCombinedVolume()).also {
        require(it >= 0) {
            val baseStr = "$baseVolume (${baseVolume.volume})"
            val subtractStr = "[" + this.subtract.joinToString {
                "$it (${it.volume})"
            } + "]"
            val subtractVolume = "(${this.subtract.calculateCombinedVolume()})"
            "Base volume is smaller than subtract volume after intersecting:\n$baseStr\n-\n$subtractStr ($subtractVolume)"
        }
    }

    override fun intersectionWith(other: Volume): Volume? {
        return when (other) {
            is Cube -> CombinedVolume(baseVolume.intersectionWith(other) ?: return null, subtract)
            is CombinedVolume -> CombinedVolume(
                baseVolume.intersectionWith(other.baseVolume) ?: return null,
                subtract + other.subtract
            )
        }.let { if (it.volume == 0L) null else it }
    }

    override fun minus(other: Volume): Volume {
        if (other !is Cube) TODO()

        return CombinedVolume(baseVolume, subtract + other)
    }
}

data class Cube(
    val x: LongRange,
    val y: LongRange,
    val z: LongRange,
) : Volume {
    override val volume: Long = (x.last - x.first + 1) * (y.last - y.first + 1) * (z.last - z.first + 1)

    fun intersectionWith(other: Cube): Cube? {
        val x = x.intersectingRange(other.x) ?: return null
        val y = y.intersectingRange(other.y) ?: return null
        val z = z.intersectingRange(other.z) ?: return null
        return Cube(x, y, z)
    }

    override fun intersectionWith(other: Volume): Volume? {
        return when (other) {
            is Cube -> intersectionWith(other)
            is CombinedVolume -> CombinedVolume(
                this.intersectionWith(other.baseVolume) ?: return null,
                other.subtract
            )
        }
    }

    override fun minus(other: Volume): Volume {
        if (other !is Cube) TODO()

        return CombinedVolume(this, listOf(other))
    }
}

fun LongRange.intersectingRange(other: LongRange): LongRange? {
    return if (this.first > other.last || this.last < other.first) {
        null
    } else {
        LongRange(max(this.first, other.first), min(this.last, other.last))
    }
}

data class Procedure(
    val on: Boolean,
    val x: LongRange,
    val y: LongRange,
    val z: LongRange,
)

class Sparse3DGrid {
    private val turnedOn = mutableSetOf<Volume>()

    fun turnOff(cube: Cube) {
        val toReplace = turnedOn.filter { it.intersects(cube) }.toSet()
        turnedOn.removeAll(toReplace)
        turnedOn.addAll(toReplace.map { it - cube }.filter { it.volume > 0L })
    }

    fun turnOn(cube: Cube) {
        turnedOn.add(cube)
    }

    fun calculateVolume(): Long {
        return turnedOn.toList().calculateCombinedVolume()
    }
}

private val volumeCache = mutableMapOf<List<Volume>, Long>()

fun List<Volume>.calculateCombinedVolume(): Long {
    if (this.isEmpty()) return 0L
    if (this.size == 1) return single().volume
    if (volumeCache.containsKey(this)) return volumeCache[this]!!

    var volume = 0L
    forEachIndexed { index, v ->
        val prev = slice(0 until index)
        val prevIntersections = prev.mapNotNull { v.intersectionWith(it) }

        volume += v.volume
        volume -= prevIntersections.calculateCombinedVolume()
    }

    return volume.also {
        volumeCache[this] = it
    }
}

fun main() {
    val input = getInput(22, example = false).filterNot { it.isBlank() }
    val coord = "(-?\\d+)"
    val regex = Regex("(on|off) x=$coord..$coord,y=$coord..$coord,z=$coord..$coord")

    val steps = input.map {
        regex.matchEntire(it)!!.destructured.let { (on, xFrom, xTo, yFrom, yTo, zFrom, zTo) ->
            val mode = on == "on"
            val x = xFrom.toLong()..xTo.toLong()
            val y = yFrom.toLong()..yTo.toLong()
            val z = zFrom.toLong()..zTo.toLong()
            Procedure(mode, x, y, z)
        }
    }.filter {
        it.x.first >= -50 && it.x.last <= 50 && it.y.first >= -50 && it.y.last <= 50 && it.z.first >= -50 && it.z.last <= 50
    }

    val grid = Sparse3DGrid()
    steps.forEach {
        if (it.on) {
            grid.turnOn(Cube(it.x, it.y, it.z))
        } else {
            grid.turnOff(Cube(it.x, it.y, it.z))
        }
        println("Did $it")
        println("v=${grid.calculateVolume()}")
    }
}
