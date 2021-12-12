package at.robbert.adventofcode

data class Cave(
    val name: String,
    val large: Boolean,
    val connections: MutableSet<Cave>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cave

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return "Cave(name='$name', large=$large)"
    }

}

fun main() {
    val input = getInput(12, example = false)

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
    val paths = findAllPaths(start)

    paths.forEach {
        println(it.joinToString("->") { it.name })
    }
    println()
    println(paths.size)
}

fun findAllPaths(origin: Cave, skip: Set<String> = emptySet()): List<List<Cave>> {
    if (origin.name == "end") return listOf(listOf(origin))

    return origin.connections.filter { it.name !in skip }.flatMap {
        findAllPaths(
            it,
            if (origin.large) skip else skip + origin.name
        ).map { path ->
            listOf(origin) + path
        }
    }
}
