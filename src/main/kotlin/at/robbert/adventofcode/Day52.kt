package at.robbert.adventofcode

import kotlin.math.abs

fun main() {
    val input = getInput(5, example = false)

    val r = Regex("(\\d+),(\\d+) -> (\\d+),(\\d+)")
    val lines = input.mapNotNull {
        val match = r.matchEntire(it)
        if (match != null) {
            val (x1, y1, x2, y2) = match.destructured
            Line(Point(x1.toInt(), y1.toInt()), Point(x2.toInt(), y2.toInt()))
        } else {
            null
        }
    }

    val points: MutableMap<Point, Int> = mutableMapOf()
    lines.forEach {
        val stepX = when {
            it.p1.x > it.p2.x -> -1
            it.p1.x < it.p2.x -> 1
            else -> 0
        }
        val stepY = when {
            it.p1.y > it.p2.y -> -1
            it.p1.y < it.p2.y -> 1
            else -> 0
        }
        val steps = maxOf(abs(it.p1.x - it.p2.x), abs(it.p1.y - it.p2.y)) + 1

        var currentX = it.p1.x
        var currentY = it.p1.y
        for (i in 0 until steps) {
            points[Point(currentX, currentY)] = points.getOrDefault(Point(currentX, currentY), 0) + 1
            currentX += stepX
            currentY += stepY
        }
    }

    debugOutput(points)

    println(points.filter { it.value > 1 }.size)
}
