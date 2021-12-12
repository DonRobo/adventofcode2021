package at.robbert.adventofcode

fun main() {
    val input = getInput(9, example = false)
    val grid = List2D(input.flatMap { it.map { it.digitToInt() } }, input.first().length)

    val basins = List2D(input.flatMap { it.map { 0 } }, input.first().length)
    val basinCount = mutableListOf<Point>()
    for (y in 0 until grid.height) {
        for (x in 0 until grid.width) {
            if (grid[x, y] == 9) continue
            val dest = findLowestPointFrom(Point(x, y), grid)
            val index = basinCount.indexOf(dest)
            if (index < 0) {
                basinCount.add(dest)
                basins[x, y] = basinCount.size
            } else {
                basins[x, y] = index + 1
            }
        }
    }
    for (y in 0 until grid.height) {
        for (x in 0 until grid.width) {
            val b = basins[x, y]
            if (b == 0) print(' ')
            else print(('!'..'~')[b - 1])
        }
        println()
    }

    val basinSizes = (1..basinCount.size).map { basinNo ->
        basins.count { it == basinNo }
    }.sortedDescending()

    println(basinSizes)
    println(basinSizes[0] * basinSizes[1] * basinSizes[2])
}

private operator fun CharRange.get(index: Int): Char {
    if (index !in 0 until this.count()) return '?'
    return this.elementAt(index)
}

fun findLowestPointFrom(point: Point, grid: List2D<Int>, excluded: Set<Point> = emptySet()): Point {
    val surroundingPoints = listOf(
        Point(point.x - 1, point.y) to grid.getOptional(point.x - 1, point.y),
        Point(point.x + 1, point.y) to grid.getOptional(point.x + 1, point.y),
        Point(point.x, point.y - 1) to grid.getOptional(point.x, point.y - 1),
        Point(point.x, point.y + 1) to grid.getOptional(point.x, point.y + 1),
        point to grid.getOptional(point.x, point.y)
    )
        .mapNotNull { if (it.second == null) null else it.first to it.second!! }
        .filter { it.first !in excluded }

    val next = surroundingPoints.minByOrNull { it.second }!!.first
    return if (next == point) next
    else findLowestPointFrom(next, grid, excluded + point)
}
