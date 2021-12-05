package at.robbert.adventofcode

fun getInput(day: Int, example: Boolean = false): List<String> {
    val name = if (example) {
        "day${day}_example.txt"
    } else {
        "day$day.txt"
    }

    val input = ClassLoader.getSystemResource(name) ?: if (example) return getInput(day, example = false)
    else
        throw IllegalStateException("Could not find $name")
    return input.readText().lines()
}
