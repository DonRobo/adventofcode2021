package at.robbert.adventofcode

fun main() {
    val input = getInput(8, example = true).filterNot { it.isBlank() }

    val entries = input.map { parseSevenSegmentEntry(it) }
    println(entries.sumOf { it.toIntOrNull() ?: 0 })
}
