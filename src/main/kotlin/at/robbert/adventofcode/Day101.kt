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
                Incomplete -> println("Line incomplete")
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
object Incomplete : ParsingResult

fun parseChunk(line: String): ParsingResult {
    val start = line[0]
    val expectedClosing = closeCharacters[openCharacters.indexOf(start)]

    println("Parsing chunk \"$line\". Starting with $start and should end with $expectedClosing")

    require(start in openCharacters) { "Invalid start character: $start" }

    var i = 1
    while (i < line.length) {
        when (val c = line[i]) {
            in openCharacters -> i = parseChunk(line.substring(i)).let {
                when (it) {
                    is Parsed -> it.nextChunk + i
                    is IllegalCharacter, is Incomplete -> return it
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

    return Incomplete
}

