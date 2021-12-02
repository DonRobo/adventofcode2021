package at.robbert.adventofcode

fun main() {
    val inputLines = getInput(1)
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
