package at.robbert.adventofcode

private val digits = arrayOf(
    "abcefg", //0
    "cf", //1
    "acdeg", //2
    "acdfg", //3
    "bcdf", //4
    "abdfg", //5
    "abdefg", //6
    "acf", //7
    "abcdefg", //8
    "abcdfg", //9
)

fun parseSevenSegmentEntry(entry: String): String {
    val split = entry.split(" | ")
    require(split.size == 2)
    val training = split[0].split(" ")
    val data = split[1].split(" ")

    val mappings = mutableMapOf<Char, List<Char>>().withDefault { "abcdefg".toList() }

    var mappedSomething = false
    fun parseDigit(digitString: String): Int {
        val options = digits.mapIndexed { d, string -> d to string }.filter { (_, string) ->
            string.length == digitString.length
        }

        return TODO()
    }

    do {
        mappedSomething = false
        (training + data).forEach {
            parseDigit(it)
        }
    } while (mappedSomething)

    return TODO()
}

fun main() {
    val input = getInput(8, example = false).filterNot { it.isBlank() }

    val entries = input.map { parseSevenSegmentEntry(it) }
    println(entries.sumOf { entry -> entry.count { digit -> digit in listOf('1', '4', '7', '8') } })
}
