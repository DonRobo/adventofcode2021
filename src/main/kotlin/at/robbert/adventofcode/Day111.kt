package at.robbert.adventofcode

fun main() {
    val input = getInput(11, example = false)

    val octopi = List2D(input.flatMap { it.map { it.digitToInt() } }, input.first().length)
    val flashed = List2D(octopi.width, octopi.height) { x, y ->
        false
    }

    fun printOctopi() {
        for (y in 0 until octopi.height) {
            for (x in 0 until octopi.width) {
                print(octopi[x, y])
            }
            println()
        }
    }

    fun flash(x: Int, y: Int) {
        flashed[x, y] = true
        for (dy in -1..1) {
            for (dx in -1..1) {
                if (dx == 0 && dy == 0) continue
                val nx = x + dx
                val ny = y + dy
                if (nx in 0 until octopi.width && ny in 0 until octopi.height) {
                    octopi[nx, ny] = octopi[nx, ny] + 1
                }
            }
        }
    }

    printOctopi()

    var flashes = 0
    repeat(100) {
        for (y in 0 until octopi.height) {
            for (x in 0 until octopi.width) {
                flashed[x, y] = false
                octopi[x, y] = octopi[x, y] + 1
            }
        }
        var done = false
        while (!done) {
            done = true
            for (y in 0 until octopi.height) {
                for (x in 0 until octopi.width) {
                    if (octopi[x, y] > 9 && !flashed[x, y]) {
                        flash(x, y)
                        done = false
                        flashes++
                    }
                }
            }
        }

        for (y in 0 until octopi.height) {
            for (x in 0 until octopi.width) {
                if (flashed[x, y]) {
                    octopi[x, y] = 0
                }
            }
        }

        println()
        println("Step ${it + 1}")
        printOctopi()
    }

    println()
    println("Got $flashes flashes")
}
