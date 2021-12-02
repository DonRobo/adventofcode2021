package at.robbert.adventofcode

fun getInput(day: Int): List<String> {
    val input = ClassLoader.getSystemResource("day$day.txt")
    return input.readText().lines()
}
