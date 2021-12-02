package at.robbert.adventofcode

fun main() {
    val inputLines = getInput(1).mapNotNull { it.toIntOrNull() }
    val slidingWindows = (0..(inputLines.size - 3)).map { i ->
        inputLines.slice(i..(i + 2)).sum()
    }

    var previous: Int? = null
    var count = 0
    slidingWindows.forEach { l ->
        if (previous != null && previous!! < l) {
            count++
        }
        previous = l
    }
    println(count)
}
