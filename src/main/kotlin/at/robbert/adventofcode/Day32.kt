package at.robbert.adventofcode

fun main() {
    val input = getInput(3).filterNot { it.isBlank() }
    val binaryNumbers = input.map { it.map { it == '1' } }

    val oxygen = findDiagnostic(binaryNumbers, ::mostCommon)
    val co2 = findDiagnostic(binaryNumbers, ::leastCommon)

    val lifeSupportRating = oxygen.toInt() * co2.toInt()

    println(lifeSupportRating)
}

fun leastCommon(numbers: List<List<Boolean>>): List<Boolean> {
    val length = numbers.first().size

    return (0 until length).map { bit ->
        val zeros = numbers.count { !it[bit] }
        val ones = numbers.count { it[bit] }
        if (zeros > ones) return@map true
        else if (ones > zeros) return@map false
        else return@map false
    }
}

fun mostCommon(numbers: List<List<Boolean>>): List<Boolean> {
    val length = numbers.first().size

    return (0 until length).map { bit ->
        val zeros = numbers.count { !it[bit] }
        val ones = numbers.count { it[bit] }
        if (zeros > ones) return@map false
        else if (ones > zeros) return@map true
        else return@map true
    }
}

private fun List<Boolean>.toInt(): Int {
    return this.joinToString("") { if (it) "1" else "0" }.toInt(2)
}

private fun findDiagnostic(
    binaryNumbers: List<List<Boolean>>,
    bitCriteria: (List<List<Boolean>>) -> List<Boolean>
): List<Boolean> {
    var numbers = binaryNumbers

    var bit = 0
    while (numbers.size > 1) {
        val bc = bitCriteria(numbers)
        numbers = numbers.filter {
            it[bit] == bc[bit]
        }
        bit++
    }

    return numbers.single()
}
