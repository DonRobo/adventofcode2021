package at.robbert.adventofcode

import java.util.*

fun main() {
    val input = getInput(15, example = false)

    val grid = List2D(input.flatMap { it.mapNotNull { it.digitToIntOrNull() } }, input.first().length)

    val best = mutableMapOf<Point, Path>()
    val start = Point(0, 0)
    val target = Point(grid.width - 1, grid.height - 1)

    var steps = 0
    val open = LinkedList<Path>()
    open.add(Path(null, start, 0))

    while (open.isNotEmpty()) {
        steps++

        if (steps % 1000 == 0) {
            println("Steps: $steps")
            println("Open: ${open.size}")
            println("Already checked: ${best.size}")
        }

        val path = open.remove()
        val bestSoFar = best[path.position]
        if (bestSoFar != null && bestSoFar.cost <= path.cost) continue
        best[path.position] = path

        val next = listOf(
            Point(path.position.x, path.position.y - 1),
            Point(path.position.x + 1, path.position.y),
            Point(path.position.x, path.position.y + 1),
            Point(path.position.x - 1, path.position.y),
        ).filter { it.x in 0 until grid.width && it.y in 0 until grid.height }.map {
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
    best[target]!!.path.forEach {
        pathGrid[it.x, it.y] = 1
    }
    for (y in 0 until pathGrid.height) {
        for (x in 0 until pathGrid.width) {
            print(if (pathGrid[x, y] > 0) '#' else ' ')
        }
        println()
    }
    println()

    println("Total risk: ${best[target]!!.cost}")
}

class Path(
    val previous: Path?,
    val position: Point,
    thisCost: Int
) {
    val cost: Int = thisCost + (previous?.cost ?: 0)

    val pathString: String get() = (previous?.let { "${it.pathString}->" } ?: "") + "[${position.x}, ${position.y}]"
    val path: List<Point> get() = (previous?.path ?: emptyList()) + position

    override fun toString(): String = "Path(cost=$cost, path=$pathString)"

}
