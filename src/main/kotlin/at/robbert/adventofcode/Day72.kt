package at.robbert.adventofcode

import kotlin.math.abs

fun main() {
    val input = getInput(7, example = false)

    val ogCrabPositions = input.single { it.isNotBlank() }.split(",").map { it.toInt() }.toIntArray()


    val min = ogCrabPositions.minOrNull()!!
    val max = ogCrabPositions.maxOrNull()!!

    val cache = mutableMapOf<Int, Int>()
    fun fuelForDistance(dist: Int): Int {
        if (cache.containsKey(dist)) return cache[dist]!!

        val ret = if (dist > 0)
            dist + fuelForDistance(dist - 1)
        else
            0

        cache[dist] = ret

        return ret
    }

    fun fuelCost(pos: Int): Int {
        return ogCrabPositions.sumOf { fuelForDistance(abs(it - pos)) }
    }

    var best = null as Int?
    for (pos in min..max) {
        val fuel = fuelCost(pos)
        if (best == null || fuel < best) {
            best = fuel
            println("$pos: $fuel")
        }
    }

    println(best)
}
