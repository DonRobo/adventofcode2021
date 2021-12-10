package at.robbert.adventofcode

fun <T : Comparable<T>> List<T>.median(): T {
    require(size % 2 == 1) { "size must be odd" }

    return sorted().let { it[it.size / 2] }
}


fun main() {
    val input = getInput(10, example = false).filterNot { it.isBlank() }

    val scores = mutableListOf<Long>()
    input.forEach { line ->
        parseChunk(line).let {
            when (it) {
                is IllegalCharacter -> println("Illegal character: ${it.character}, doesn't matter")
                is Incomplete -> {
                    "Incomplete line: $line, missing: ${it.missingString}"
                    scores += it.score
                }
                is Parsed -> println("Line okay")
            }
        }
    }

    println("==Scores==")
    scores.sorted().forEach(::println)

    println("Score: ${scores.median()}")
}
