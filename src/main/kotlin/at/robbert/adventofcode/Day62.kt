package at.robbert.adventofcode

fun main() {
    val input = getInput(6, example = false)

    var fish = LongArray(9) { 0 }

    input.first().split(",").forEach {
        val days = it.toInt()
        fish[days]++
    }

    repeat(256) {
        val newFish = LongArray(9) { 0 }
        (0..8).forEach { day ->
            if (day == 0) {
                newFish[8] += fish[0]
                newFish[6] += fish[0]
            } else {
                newFish[day - 1] += fish[day]
            }
        }
        fish = newFish
    }

    println("Fishes: ${fish.sum()}")
}

private fun debugOutput(fish: LongArray) {
    fish.forEachIndexed { day, count ->
        println("$count on day $day")
    }
}
