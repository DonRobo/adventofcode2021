package at.robbert.adventofcode

fun main() {
    val input = getInput(15, example = true)

    val grid = List2D(input.flatMap { it.mapNotNull { it.digitToIntOrNull() } }, input.first().length)

    val best = mutableMapOf<Point, Int>()
    val start = Point(0, 0)
    val target = Point(grid.width - 1, grid.height - 1)

    best[start] = 0
    var steps = 0
    fun pathFind(path: Path?): Path? {
        steps++

        val from = path?.position ?: start

        val north = Point(from.x, from.y - 1)
        val east = Point(from.x + 1, from.y)
        val south = Point(from.x, from.y + 1)
        val west = Point(from.x - 1, from.y)

        val options = listOf(north, east, south, west)
            .filter {
                it.x in 0 until grid.width && it.y in 0 until grid.height
            }

        val paths = options.map { point ->
            Path(path, point, grid[point.x, point.y])
        }.filter {
            val bestSoFar = best[it.position]
            val newBest = bestSoFar == null || bestSoFar >= it.cost
            newBest
        }.sortedBy { it.cost }

        paths.forEach {
            best[it.position] = it.cost
        }

        paths.firstOrNull { it.position == target }?.let {
            println("Step $steps: Found $it")
            return it
        }

        return paths.mapNotNull { pathFind(it) }.minByOrNull { it.cost }
    }

    val found = pathFind(null) ?: error("Path finding failed")

    for (y in 0 until grid.height) {
        for (x in 0 until grid.width) {
            print(grid[x, y])
        }
        println()
    }
    println()
    val pathGrid = List2D((0 until grid.width * grid.height).map { 0 }, grid.width)
    found.path.forEach {
        pathGrid[it.x, it.y] = 1
    }
    for (y in 0 until pathGrid.height) {
        for (x in 0 until pathGrid.width) {
            print(if (pathGrid[x, y] > 0) '#' else ' ')
        }
        println()
    }
    println()
}

class Path(
    val previous: Path?,
    val position: Point,
    private val thisCost: Int
) {
    val cost: Int get() = thisCost + (previous?.cost ?: 0)

    val pathString: String get() = (previous?.let { "${it.pathString}->" } ?: "") + "[${position.x}, ${position.y}]"
    val path: List<Point> get() = (previous?.path ?: emptyList()) + position

    override fun toString(): String = "Path(cost=$cost, path=$pathString)"

}
