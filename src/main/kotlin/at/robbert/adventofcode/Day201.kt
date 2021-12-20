package at.robbert.adventofcode

import kotlin.system.measureTimeMillis

private fun String.requireImageString() = forEach { require(it in listOf('.', '#')) }

class ImageEnhancer(private val algo: String) {

    init {
        algo.requireImageString()
        require(algo.length == 512)
    }

    fun enhance(pixels: String): Char {
        pixels.requireImageString()

        val binary = pixels.map {
            when (it) {
                '.' -> '0'
                '#' -> '1'
                else -> error("Invalid pixel: $it")
            }
        }.joinToString("")

        val dec = binary.toInt(2)
        require(dec in 0 until 512)
        return algo[dec]
    }
}

class SparseImage(lines: List<String>? = null, private val defaultPixel: Char = '.') {
    private val nonDefaultPixel get() = if (defaultPixel == '.') '#' else '.'

    private val nonDefaultPixels = mutableSetOf<Point>()

    constructor(pixels: Collection<Point>, defaultPixel: Char = '.') : this(null, defaultPixel) {
        nonDefaultPixels.addAll(pixels)
    }

    init {
        if (lines != null) {
            val width = lines.first().length

            lines.forEachIndexed { y, line ->
                require(line.length == width)
                line.requireImageString()

                line.forEachIndexed { x, p ->
                    if (p != defaultPixel) {
                        nonDefaultPixels.add(Point(x, y))
                    }
                }
            }
        }
    }

    fun render() {
        val minX = nonDefaultPixels.minByOrNull { it.x }!!.x
        val maxX = nonDefaultPixels.maxByOrNull { it.x }!!.x
        val minY = nonDefaultPixels.minByOrNull { it.y }!!.y
        val maxY = nonDefaultPixels.maxByOrNull { it.y }!!.y

        for (y in (minY - 3)..(maxY + 3)) {
            for (x in (minX - 3)..(maxX + 3)) {
                print(if (nonDefaultPixels.contains(Point(x, y))) nonDefaultPixel else defaultPixel)
            }
            println()
        }

        println()
        if (defaultPixel == '#') {
            println("Infinite pixels lit (${nonDefaultPixels.size} unlit)")
        } else {
            println("${nonDefaultPixels.size} pixels lit")
        }
    }

    fun enhance(algo: ImageEnhancer): SparseImage {
        val minX = nonDefaultPixels.minByOrNull { it.x }!!.x
        val maxX = nonDefaultPixels.maxByOrNull { it.x }!!.x
        val minY = nonDefaultPixels.minByOrNull { it.y }!!.y
        val maxY = nonDefaultPixels.maxByOrNull { it.y }!!.y

        val newPixels = mutableSetOf<Point>()

        val newDefaultPixel = algo.enhance((0 until 9).map { defaultPixel }.joinToString(""))
        for (y in (minY - 4)..(maxY + 4)) {
            for (x in (minX - 4)..(maxX + 4)) {
                if (algo.enhance(getPixels(x - 1, x + 1, y - 1, y + 1)) != newDefaultPixel) {
                    newPixels.add(Point(x, y))
                }
            }
        }

        return SparseImage(newPixels, newDefaultPixel)
    }

    private fun getPixels(fromX: Int, toX: Int, fromY: Int, toY: Int): String {
        return (fromY..toY).joinToString("") { y ->
            (fromX..toX).joinToString("") { x ->
                (if (nonDefaultPixels.contains(Point(x, y))) nonDefaultPixel else defaultPixel).toString()
            }
        }.also { it.requireImageString() }
    }
}

fun main() {
    val input = getInput(20, example = false)

    val algo = ImageEnhancer(input.first { it.isNotBlank() })

    val image = SparseImage(input.slice(1 until input.size).filter { it.isNotBlank() })
    image.render()

    var currentImage = image
    measureTimeMillis {
        for (step in 0 until 50) {
            currentImage = currentImage.enhance(algo)
            println("Step ${step + 1}")
            currentImage.render()
        }
    }.also {
        println("Total time: $it ms")
    }
}
