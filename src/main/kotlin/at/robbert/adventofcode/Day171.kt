package at.robbert.adventofcode


fun main() {
    val input = getInput(17, example = false).filterNot { it.isBlank() }

    val r = Regex("target area: x=(-?\\d+)..(-?\\d+), y=(-?\\d+)..(-?\\d+)")
    val (_, targetXTo, targetYFrom, targetYTo) = r.matchEntire(input.single())?.groupValues?.subList(1, 5)
        ?.map { it.toInt() }
        ?: error("no match")

    fun renderSimulation(vx: Int, vy: Int) {
        var vxC = vx
        var vyC = vy
        var xC = 0
        var yC = 0
        val points = mutableListOf<Point>()
        while (points.none { it.x > targetXTo } || points.none { it.y < targetYFrom }) {
            xC += vxC
            yC += vyC

            if (vxC > 0) vxC-- else vxC++
            vyC--

            points.add(Point(xC, yC))
        }
        val ys = (points.map { it.y } + listOf(0, targetYTo, targetYFrom))

        val maxY = ys.maxOrNull()!!

        /*
        for (y in (minY..maxY).reversed()) {
            for (x in minX..maxX) {
                val p = Point(x, y)
                if (x == 0 && y == 0) {
                    print('S')
                } else if (points.contains(p)) {
                    print('#')
                } else if (x in targetXFrom..targetXTo && y in targetYFrom..targetYTo) {
                    print('T')
                } else {
                    print('.')
                }
            }
            println()
        }*/
        println("Max y: $maxY")
    }

    renderSimulation(2, 74)
}
