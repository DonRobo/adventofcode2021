package at.robbert.adventofcode

fun main() {
    val input = getInput(10, example = false).filterNot { it.isBlank() }

    var score = 0
    input.forEach { line ->
        parseChunk(line).let {
            when (it) {
                is IllegalCharacter -> score += when (it.character) {
                    ')' -> 3
                    ']' -> 57
                    '}' -> 1197
                    '>' -> 25137
                    else -> error("Invalid invalid character ${it.character}")
                }
                is Parsed -> println("Line okay")
                is Incomplete -> println("Line incomplete")
            }
        }
    }
    println("Score: $score")
}

val openCharacters = listOf('{', '<', '(', '[')
val closeCharacters = listOf('}', '>', ')', ']')

sealed interface ParsingResult

data class IllegalCharacter(val character: Char) : ParsingResult
data class Parsed(val nextChunk: Int) : ParsingResult
data class Incomplete(val missing: Char, val previousIncomplete: Incomplete?) : ParsingResult {
    val missingString: String get() = (previousIncomplete?.missingString ?: "") + missing

    val score: Long
        get() = (previousIncomplete?.score ?: 0) * 5L + when (missing) {
            ')' -> 1
            ']' -> 2
            '}' -> 3
            '>' -> 4
            else -> error("Invalid missing character $missing")
        }
}

fun parseChunk(line: String): ParsingResult {
    val start = line[0]
    val expectedClosing = closeCharacters[openCharacters.indexOf(start)]

    println("Parsing chunk \"$line\". Starting with $start and should end with $expectedClosing")

    require(start in openCharacters) { "Invalid start character: $start" }

    var incomplete: Incomplete? = null
    var i = 1
    while (i < line.length) {
        when (val c = line[i]) {
            in openCharacters -> i = parseChunk(line.substring(i)).let {
                when (it) {
                    is Parsed -> it.nextChunk + i
                    is IllegalCharacter -> return it
                    is Incomplete -> {
                        incomplete = it
                        line.length
                    }
                }
            }
            in closeCharacters -> return if (c == expectedClosing) {
                println("Chunk parsed: ${line.substring(0, i + 1)}")
                Parsed(i + 1)
            } else {
                println("Expected closing $expectedClosing, got $c")
                IllegalCharacter(c)
            }
            else -> error("Invalid character: $c")
        }
    }

    print("Chunk incomplete: ")
    return Incomplete(expectedClosing, incomplete).also {
        println(it.missingString + " (${it.score})")
    }
}

