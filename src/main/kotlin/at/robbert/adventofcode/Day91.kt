package at.robbert.adventofcode

class List2D<T>(data: Collection<T>, val width: Int) {
    constructor(width: Int, height: Int, init: (Int, Int) -> T) : this(width.let {
        val generatedData = ArrayList<T>(width * height)
        (0 until height).map { y ->
            (0 until width).map { x ->
                generatedData.add(init(x, y))
            }
        }
        generatedData
    }, width)

    private val data = data.toMutableList()

    val height: Int = data.size / width

    init {
        require(data.size % width == 0)
    }

    operator fun get(x: Int, y: Int): T {
        require(x in 0 until width)
        require(y in 0 until height)
        return data[x + y * width]
    }

    fun getOptional(x: Int, y: Int): T? {
        if (x < 0 || x >= width || y < 0 || y >= height) return null
        return data[x + y * width]
    }

    operator fun set(x: Int, y: Int, value: T) {
        data[x + y * width] = value
    }

    fun count(block: (T) -> Boolean): Int {
        return data.count(block)
    }

    fun asList(): List<T> {
        return data
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
