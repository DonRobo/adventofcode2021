package at.robbert.adventofcode

fun main() {
    val resource = ClassLoader.getSystemResource("day1.txt")
    val inputLines = resource.readText().lines()
    var previousLine: Int? = null
    var count = 0
    inputLines.mapNotNull { it.toIntOrNull() }.forEach { l ->
        if (previousLine != null && previousLine!! < l) {
            count++
        }
        previousLine = l
    }
    println(count)
}
