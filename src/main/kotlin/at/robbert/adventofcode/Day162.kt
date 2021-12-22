package at.robbert.adventofcode

fun prettyPrintPacket(packet: Packet, intention: Int = 0) {
    val intentionStr = "  ".repeat(intention)
    when (packet) {
        is LiteralPacket -> println("${intentionStr}${packet.literal}")
        is OperatorPacket -> {
            val operatorStr = when (packet.type) {
                0 -> "SUM"
                1 -> "PROD"
                2 -> "MIN"
                3 -> "MAX"
                5 -> "GREATER"
                6 -> "LESS"
                7 -> "EQUALS"
                else -> "ERROR"
            }
            println("${intentionStr}$operatorStr (${packet.calculateValue()})")
            packet.subPackets.forEach {
                prettyPrintPacket(it, intention + 1)
            }
        }
    }
}

fun Packet.calculateValue(): Long {
    if (type == 4) return (this as LiteralPacket).literal
    this as OperatorPacket

    val subValues = subPackets.map { it.calculateValue() }

    return when (type) {
        0 -> subValues.sumOf { it }
        1 -> subValues.fold(1L) { acc, it -> acc * it }
        2 -> subValues.minOrNull() ?: error("Min packet can't be empty")
        3 -> subValues.maxOrNull() ?: error("Max packet can't be empty")
        5 -> {
            require(subValues.size == 2)
            return if (subValues[0] > subValues[1]) 1 else 0
        }
        6 -> {
            require(subValues.size == 2)
            return if (subValues[0] < subValues[1]) 1 else 0
        }
        7 -> {
            require(subValues.size == 2)
            return if (subValues[0] == subValues[1]) 1 else 0
        }
        else -> {
            println("Couldn't evaluate packet:")
            prettyPrintPacket(this)
            error("Evaluation error")
        }
    }
}

fun main() {
    val input = getInput(16, example = false)

    val binary = input.first().map {
        val value = it.digitToInt(16)
        value.toString(2).padStart(4, '0')
    }.joinToString("")

    val packet = readPacket(ConsumableBinary(binary))
    prettyPrintPacket(packet)
    println(packet.calculateValue())
}
