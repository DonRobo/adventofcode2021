package at.robbert.adventofcode

class List2D<T>(data: Collection<T>, val width: Int) {
    private val data = data.toList()

    operator fun get(x: Int, y: Int): T {
        return data[x + y * width]
    }

    val height: Int = data.size / width

    init {
        require(data.size % width == 0)
    }
}

fun main() {
    val input = getInput(9, example = false)
    val grid = List2D(input.flatMap { it.map { it.digitToInt() } }, input.first().length)

    var sum = 0
    for (y in 0 until grid.height) {
        for (x in 0 until grid.width) {
            val left = if (x == 0) 10 else grid[x - 1, y]
            val right = if (x == grid.width - 1) 10 else grid[x + 1, y]
            val up = if (y == 0) 10 else grid[x, y - 1]
            val down = if (y == grid.height - 1) 10 else grid[x, y + 1]
            val current = grid[x, y]
            if (current < left && current < right && current < up && current < down) {
                sum += current + 1
            }
        }
    }
    println(sum)
}
