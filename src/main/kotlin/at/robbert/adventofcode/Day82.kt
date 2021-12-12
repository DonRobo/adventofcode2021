package at.robbert.adventofcode

fun main() {
    val input = getInput(8, example = false).filterNot { it.isBlank() }

    val entries = input.map { parseSevenSegmentEntry(it) }
    entries.forEachIndexed { index, s ->
        println(s)
        if (s.toIntOrNull() == null) {
            println("\t${input[index]}")
        }
    }
    require(entries.all { it.toIntOrNull() != null }) { "Not all numbers were found" }
    println("Sum:")
    println(entries.sumOf { it.toIntOrNull() ?: 0 })
}
