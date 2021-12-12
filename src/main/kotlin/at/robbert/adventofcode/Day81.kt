package at.robbert.adventofcode

private val allChars = 'a'..'g'

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

    val exclusions = mutableMapOf<Char, MutableSet<Char>>()

    fun exclusionsFor(c: Char): Set<Char> {
        return exclusions.getOrDefault(c, mutableSetOf())
    }

    fun inclusionsFor(c: Char): Set<Char> {
        val excl = exclusionsFor(c)
        return allChars.filter { it !in excl }.toSet()
    }

    fun calculateOptions(number: String): Set<String> {
        return digits
            .filter { it.length == number.length }
            .filter { digitOption ->
                number.all {
                    inclusionsFor(it).any { i ->
                        i in digitOption
                    }
                }
            }
            .filter {
                val groups = number.map {
                    inclusionsFor(it)
                }.groupBy { it }.mapValues { it.value.size }
                groups.all { (chars, needed) ->
                    it.count { it in chars } >= needed
                }
            }
            .toSet()
    }

    fun addExclusions(c: Char, exclusionsToAdd: Set<Char>): Boolean {
        if (exclusionsToAdd.isEmpty()) return false

        val set = exclusions.computeIfAbsent(c) { mutableSetOf() }
        val exclToPrint =
            exclusionsToAdd.filter { it !in set }
        if (exclToPrint.isNotEmpty())
            println("That means $c can't be ${exclToPrint.joinToString(" or ") { it.toString() }}")
        return set.addAll(exclusionsToAdd)
    }

    fun couldBeMappedByNoneOf(targetLetter: Char, lettersToBeMapped: List<Char>): Boolean {
        return lettersToBeMapped.all {
            exclusionsFor(it).contains(targetLetter)
        }
    }

    var changed = true
    while (changed) {
        println("===New iteration===")
        allChars.forEach { c ->
            val cExcl = exclusionsFor(c)
            val leftOver = allChars.filter { it !in cExcl }
            println("$c can only be ${leftOver.joinToString(" or ") { it.toString() }}")
        }
        changed = false

        for (number in (training + data)) {
            val options = calculateOptions(number)
            require(options.isNotEmpty()) {
                "How can there not be any options?!"
            }
            println("Number '$number' can only be mapped to ${options.joinToString(" or ") { it }}")
            val lettersThatNeverAppear = allChars.filter { c ->
                options.none { it.contains(c) }
            }
            val targetLetters = allChars.filter { options.all { o -> it in o } }.toSet()

            val mappings = number.map { it to inclusionsFor(it) }.toMap()
            val reversed = mappings.toList().groupBy { it.second }.mapValues { it.value.map { it.first } }

            val mightBeInteresting = reversed.filter { it.value.size == it.key.size && it.key.size < allChars.count() }

            mightBeInteresting.forEach { (mappedTo, by) ->
                println("$mappedTo can only be mapped by $by")
            }

            number.forEach { c ->
                val superExtraExclusions =
                    mightBeInteresting.filter { c !in it.value }.flatMap { it.key }.toSet()

                val otherLetters = number.filter { it != c }.toList()
                val otherLetterNonOptions = targetLetters.filter { couldBeMappedByNoneOf(it, otherLetters) }
                val extraInclusions = allChars.filter { it in otherLetterNonOptions && it in number }
                require(extraInclusions.size <= 1)
                val extraExclusions =
                    if (extraInclusions.size == 1) allChars.filter { it !in extraInclusions } else emptyList()

                if (extraExclusions.isNotEmpty() && extraExclusions.any { it !in exclusionsFor(c) })
                    println("Extra exclusions for $c: $extraExclusions")

                if (superExtraExclusions.isNotEmpty() && superExtraExclusions.any { it !in exclusionsFor(c) }) {
                    println("Super extra exclusion: $superExtraExclusions")
                }

                if (lettersThatNeverAppear.isNotEmpty() && lettersThatNeverAppear.any { it !in exclusionsFor(c) }) {
                    println("$c can't be ${lettersThatNeverAppear.joinToString(" or ") { it.toString() }} because they never appear")
                }

                if (addExclusions(c, (lettersThatNeverAppear + extraExclusions + superExtraExclusions).toSet())) {
                    println("=>CHANGE!")
                    changed = true
                }
            }
        }
    }

    return data.joinToString("") { dataDigit ->
        val options = calculateOptions(dataDigit)

        if (options.size != 1) {
            "?"
        } else {
            digits.indexOf(options.single()).toString()
        }
    }
}

fun main() {
    val input = getInput(8, example = false).filterNot { it.isBlank() }

    val entries = input.map { parseSevenSegmentEntry(it) }
    println(entries.sumOf { entry -> entry.count { digit -> digit in listOf('1', '4', '7', '8') } })
}
