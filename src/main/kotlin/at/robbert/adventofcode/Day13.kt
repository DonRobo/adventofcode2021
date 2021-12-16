package at.robbert.adventofcode

fun main() {
    val input = getInput(13, example = false)

    val points = mutableSetOf<Point>()
    val instructions = mutableListOf<Pair<Char, Int>>()

    input.forEach {
        if (it.matches(Regex("\\d+,\\d+"))) {
            val (x, y) = it.split(",").map { it.toInt() }
            points.add(Point(x, y))
        } else {
            val r = Regex("fold along ([xy])=(\\d+)")
            val (xy, value) = r.matchEntire(it)?.destructured ?: return@forEach
            instructions.add(xy.single() to value.toInt())
        }
    }

    fun printPoints() {
        val xs = points.map { it.x }
        val minX = xs.minOrNull()!!
        val maxX = xs.maxOrNull()!!

        val ys = points.map { it.y }
        val minY = ys.minOrNull()!!
        val maxY = ys.maxOrNull()!!

        if (maxX - minX < 120)
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    if (points.contains(Point(x, y))) {
                        print("#")
                    } else {
                        print(".")
                    }
                }
                println()
            }
        println("${points.size} points")
    }

    printPoints()

    instructions.forEach { (axis, value) ->
        val pointsToFold = points.filter {
            when (axis) {
                'x' -> it.x > value
                'y' -> it.y > value
                else -> error("$axis is not an axis")
            }
        }.toSet()
        points.removeAll(pointsToFold)
        pointsToFold.forEach {
            if (axis == 'x')
                points.add(Point(value * 2 - it.x, it.y))
            else
                points.add(Point(it.x, value * 2 - it.y))
        }
        println("fold along $axis=$value")
        printPoints()
    }
}
