package at.robbert.adventofcode

fun main() {
    val input = getInput(3).filterNot { it.isBlank() }
    val length = input.first().length
    val gamma = (0 until length).map { position ->
        when (input.map {
            if (position !in it.indices) error("$it does not have $position")
            it[position]
        }.groupBy { it }.maxByOrNull { it.value.size }!!.key) {
            '0' -> 0
            '1' -> 1
            else -> error("Unknown binary digit")
        }
    }
    val gammaValue = gamma.joinToString("").toInt(2)
    val epsilonValue = gamma.joinToString("") {
        when (it) {
            1 -> 0.toString()
            0 -> 1.toString()
            else -> error("Unknown binary digit $it")
        }
    }.toInt(2)

    println(gammaValue * epsilonValue)
}
