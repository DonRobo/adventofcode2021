package at.robbert.adventofcode

fun main() {
    val input = getInput(2)
    var distance = 0
    var depth = 0

    val inputRegex = Regex("(\\w+) (\\d+)")

    input.forEach {
        val match = inputRegex.matchEntire(it) ?: return@forEach
        val command = match.groupValues[1]
        val parameter = match.groupValues[2].toInt()

        when (command) {
            "forward" -> distance += parameter
            "up" -> depth -= parameter
            "down" -> depth += parameter
        }
    }

    println(distance * depth)
}
