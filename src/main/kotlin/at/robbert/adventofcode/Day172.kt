package at.robbert.adventofcode


fun main() {
    val input = getInput(17, example = false).filterNot { it.isBlank() }

    val r = Regex("target area: x=(-?\\d+)..(-?\\d+), y=(-?\\d+)..(-?\\d+)")
    val (targetXFrom, targetXTo, targetYFrom, targetYTo) = r.matchEntire(input.single())?.groupValues?.subList(1, 5)
        ?.map { it.toInt() }
        ?: error("no match")

    var hits = 0
    for (yOption in -100..100) {
        for (xOption in 1..targetXTo) {
            if (hitsTarget(xOption, yOption, targetXFrom, targetXTo, targetYFrom, targetYTo)) {
                hits++
                println("x=$xOption, y=$yOption works")
            }
        }
    }

    println("Found $hits options")
}

private fun hitsTarget(
    initialXV: Int,
    initialYV: Int,
    targetXFrom: Int,
    targetXTo: Int,
    targetYFrom: Int,
    targetYTo: Int
): Boolean {
    var xV = initialXV
    var yV = initialYV
    var x = 0
    var y = 0
    while (x <= targetXTo && y >= targetYFrom) {
        x += xV
        y += yV

        if (x in targetXFrom..targetXTo && y in targetYFrom..targetYTo) {
            return true
        }

        if (xV > 0) {
            xV--
        } else if (xV < 0) {
            xV++
        }
        yV--
    }

    return false
}
