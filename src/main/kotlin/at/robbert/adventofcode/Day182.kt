package at.robbert.adventofcode

fun main() {
    val input = getInput(18, example = false).filterNot { it.isBlank() }.map {
        ConsumableSnailfishNumberString(it).consumePair()
    }

    var best: SnailfishNumber? = null
    for (first in input) {
        for (second in input) {
            if (first != second) {
                val sum = (first + second)
                if (best == null || best.magnitude < sum.magnitude) {
                    best = sum
                }
            }
        }
    }

    println("Best: ${best?.magnitude}")
}
