package at.robbert.adventofcode

fun main() {
    val input = getInput(12, example = true)

    val caves = mutableMapOf<String, Cave>()

    fun getCave(caveName: String): Cave =
        caves.computeIfAbsent(caveName) { Cave(caveName, caveName[0].isUpperCase(), mutableSetOf()) }

    input.forEach { con ->
        val regex = Regex("(\\w+)-(\\w+)")
        val (from, to) = (regex.matchEntire(con) ?: return@forEach).destructured
        val fromCave = getCave(from)
        val toCave = getCave(to)

        fromCave.connections.add(toCave)
        toCave.connections.add(fromCave)
    }

    val start = getCave("start")
    val paths = findAllPaths2(start)

    paths.forEach {
        println(it.joinToString("->") { it.name })
    }
    println()
    println(paths.size)
}

fun findAllPaths2(origin: Cave, skip: Set<String> = emptySet(), extraCave: String? = null): List<List<Cave>> {
    if (origin.name == "end") return listOf(listOf(origin))

    return origin.connections.filter { it.name !in skip || (extraCave == null && it.name !in listOf("start", "end")) }
        .flatMap {
            findAllPaths2(
                it,
                if (origin.large) skip else skip + origin.name,
                if (it.name in skip) it.name else extraCave,
            ).map { path ->
                listOf(origin) + path
            }
        }
}
