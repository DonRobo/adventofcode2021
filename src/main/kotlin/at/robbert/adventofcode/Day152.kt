package at.robbert.adventofcode

import java.util.*
import kotlin.math.abs

fun main() {
    val input = getInput(15, example = false)

    val grid =
        List2D(input.flatMap { it.mapNotNull { it.digitToIntOrNull() } }, input.first().length).let { smallGrid ->
            val bigGrid = List2D(Array(smallGrid.width * smallGrid.height * 5 * 5) { 0 }.toList(), smallGrid.width * 5)
            for (y in 0 until bigGrid.height) {
                for (x in 0 until bigGrid.width) {
                    val smallX = x % smallGrid.width
                    val smallY = y % smallGrid.height

                    val offsetX = x / smallGrid.width
                    val offsetY = y / smallGrid.height

                    bigGrid[x, y] = ((smallGrid[smallX, smallY] + offsetX + offsetY - 1) % 9) + 1
                }
            }
            bigGrid
        }

    println("Grid size: ${grid.width}x${grid.height}=${grid.width * grid.height}")

    val best = mutableMapOf<Point, Path>()
    val start = Point(0, 0)
    val target = Point(grid.width - 1, grid.height - 1)

    var steps = 0
    val open = LinkedList<Path>()
    open.add(Path(null, start, 0))

    while (open.isNotEmpty()) {
        steps++

        val comp = Comparator.comparingInt<Path> {
            it.cost
        }

        open.sortWith(comp)

        if (steps % 10000 == 0) {
            println("Steps: $steps")
            println("Open: ${open.size}")
            println("Already checked: ${best.size}")
            println("Current distance: ${open.first().position.manhattenDistance(target)}")
        }

        val path = open.removeFirst()
        val bestSoFar = best[path.position]
        if (bestSoFar != null && bestSoFar.cost <= path.cost)
            continue
        best[path.position] = path

        val next = listOf(
            Point(path.position.x, path.position.y - 1),
            Point(path.position.x + 1, path.position.y),
            Point(path.position.x, path.position.y + 1),
            Point(path.position.x - 1, path.position.y),
        ).also {
            if (it.any { it == Point(9, 0) }) {
                println("Found it!")
            }
        }.filter { it.x in 0 until grid.width && it.y in 0 until grid.height }.map {
            Path(path, it, grid[it.x, it.y])
        }.filter {
            val other = best[it.position]
            other == null || other.cost > it.cost
        }
        open.addAll(next)
    }
    println("Took $steps steps")

    for (y in 0 until grid.height) {
        for (x in 0 until grid.width) {
            print(grid[x, y])
        }
        println()
    }
    println()
    val pathGrid = List2D((0 until grid.width * grid.height).map { 0 }, grid.width)
    val bestPath = best[target]
    if (bestPath != null)
        bestPath.path.forEach {
            pathGrid[it.x, it.y] = 1
        }
    else {
        for (y in 0 until grid.height) {
            for (x in 0 until grid.width) {
                pathGrid[x, y] = if (best[Point(x, y)] != null) 1 else 0
            }
        }
    }
    for (y in 0 until pathGrid.height) {
        for (x in 0 until pathGrid.width) {
            print(if (pathGrid[x, y] > 0) '#' else ' ')
        }
        println()
    }
    println()

    println("Total risk: ${best[target]!!.cost} (calculated in $steps steps)")
}

private fun Point.manhattenDistance(target: Point): Int {
    return abs(target.x - this.x) + abs(target.y - this.y)
}
