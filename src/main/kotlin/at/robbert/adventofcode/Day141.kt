package at.robbert.adventofcode

fun main() {
    val input = getInput(14, example = false)

    val template = input.first()

    val stepsRaw = input.slice(2 until input.size)
    val steps = stepsRaw.mapNotNull {
        val r = Regex("(\\w{2}) -> (\\w)")
        val (a, b) = (r.matchEntire(it) ?: return@mapNotNull null).destructured
        a to b
    }.toMap()

    val polymer = StringBuilder(template)
    for (step in 0 until 10) {
        val insertions = steps.flatMap { (search, insert) ->
            require(search.length == 2)
            val indices = polymer.toString().indicesOf(search)
            indices.map { (it + 1) to insert }
        }.sortedBy { it.first }
        var offset = 0
        insertions.forEach { (index, letter) ->
            polymer.insert(index + offset, letter)
            offset += letter.length
        }

        println("Step ${step + 1}: $polymer (length = ${polymer.length})")
    }

    val counts = polymer.groupBy { it }.mapValues { it.value.size }
    println()
    counts.entries.sortedBy { it.key }.forEach { (letter, count) ->
        println("$letter: $count")
    }
    println()

    val countNumbers = counts.map { it.value }
    println("Result: ${countNumbers.maxOrNull()!! - countNumbers.minOrNull()!!}")
}

private fun CharSequence.indicesOf(search: String): List<Int> {
    var start = 0
    var found = true
    val result = mutableListOf<Int>()
    while (found) {
        found = false
        val i = indexOf(search, startIndex = start)
        if (i >= 0) {
            found = true
            result.add(i)
            start = i + 1
        }
    }

    return result
}
