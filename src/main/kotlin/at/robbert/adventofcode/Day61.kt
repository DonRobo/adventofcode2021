package at.robbert.adventofcode

class Lanternfish(
    var days: Int
)

fun main() {
    val input = getInput(6, example = false)

    val fish = input.first().split(",").map { Lanternfish(it.toInt()) }.toMutableList()

    fun Lanternfish.live(): Lanternfish? {
        days--
        if (days < 0) {
            days = 6
            return Lanternfish(8)
        }

        return null
    }

    repeat(80) {
        val fishToAdd = mutableListOf<Lanternfish>()
        fish.forEach {
            it.live()?.let { newBornFish -> fishToAdd.add(newBornFish) }
        }
        fish.addAll(fishToAdd)
    }

    println("After 80 days there are ${fish.size} fish")
}
