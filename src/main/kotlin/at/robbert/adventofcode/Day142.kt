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

    var pairs = mutableMapOf<String, Long>()
    val letters = mutableMapOf<Char, Long>()

    fun addLetter(char: Char, count: Long) {
        letters[char] = (letters[char] ?: 0) + count
    }

    template.forEach { addLetter(it, 1) }

    template.windowed(2).forEach {
        pairs[it] = (pairs[it] ?: 0) + 1
    }

    println("Pairs:")
    pairs.forEach { (k, v) -> println("$k: $v") }

    repeat(40) {
        val newPairs = mutableMapOf<String, Long>()
        fun add(pair: String, count: Long) {
            newPairs[pair] = (newPairs[pair] ?: 0) + count
        }
        pairs.forEach { (pair, count) ->
            val letterToInsert = steps[pair]?.single()
            if (letterToInsert != null) {
                add(pair[0].toString() + letterToInsert, count)
                add(letterToInsert.toString() + pair[1], count)
                addLetter(letterToInsert, count)
            } else {
                add(pair, count)
            }
        }
        pairs = newPairs
        println("Pairs after step ${it + 1}:")
        pairs.forEach { (k, v) -> println("$k: $v") }
    }

    println("Letters:")
    letters.forEach { (k, v) -> println("$k: $v") }
    println()
    println("Result: ${letters.values.maxOrNull()!! - letters.values.minOrNull()!!}")
}
