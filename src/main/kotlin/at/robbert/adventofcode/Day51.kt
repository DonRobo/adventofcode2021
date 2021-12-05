package at.robbert.adventofcode

data class Point(
    val x: Int,
    val y: Int
)

data class Line(
    val p1: Point,
    val p2: Point
)

fun main() {
    val input = getInput(5, example = true)

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

    val straightLines = lines.filter { it.p1.x == it.p2.x || it.p1.y == it.p2.y }
    val points: MutableMap<Point, Int> = mutableMapOf()
    straightLines.forEach {
        val minX = minOf(it.p1.x, it.p2.x)
        val minY = minOf(it.p1.y, it.p2.y)
        val maxX = maxOf(it.p1.x, it.p2.x)
        val maxY = maxOf(it.p1.y, it.p2.y)

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                points[Point(x, y)] = points.getOrDefault(Point(x, y), 0) + 1
            }
        }
    }

    debugOutput(points)

    println(points.filter { it.value > 1 }.size)
}

fun debugOutput(points: Map<Point, Int>) {
    val minX = points.map { it.key.x }.minOrNull() ?: error("No points")
    val minY = points.map { it.key.y }.minOrNull() ?: error("No points")
    val maxX = points.map { it.key.x }.maxOrNull() ?: error("No points")
    val maxY = points.map { it.key.y }.maxOrNull() ?: error("No points")

    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val count = points[Point(x, y)] ?: 0
            print(if (count > 0) "$count" else ".")
        }
        println()
    }
}
