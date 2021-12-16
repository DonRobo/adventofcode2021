package at.robbert.adventofcode

class ConsumableBinary(private val binary: String) {
    var index = 0
        private set

    fun consume(length: Int): String {
        require(length <= binary.length - index) {
            "Cannot consume $length characters from $binary starting at $index"
        }

        val result = binary.substring(index, index + length)
        index += length
        return result
    }

    fun consumeInt(length: Int): Int {
        return consume(length).toInt(2)
    }

    fun isEmpty(): Boolean {
        return index >= binary.length
    }
}

fun main() {
    val input = getInput(16, example = false)

    val binary = input.first().map {
        val value = it.digitToInt(16)
        value.toString(2).padStart(4, '0')
    }.joinToString("")
    println(binary)

    val packet = readPacket(ConsumableBinary(binary))
    println(packet)
    println(packet.versionSum)
}

val Packet.versionSum: Int
    get() = when (this) {
        is LiteralPacket -> version
        is OperatorPacket -> version + subPackets.sumOf { it.versionSum }
    }

sealed interface Packet {
    val version: Int
    val type: Int
}

data class LiteralPacket(override val version: Int, override val type: Int, val literal: Long) : Packet
data class OperatorPacket(
    override val version: Int,
    override val type: Int,
    val subPackets: List<Packet>
) : Packet

fun readPacket(binary: ConsumableBinary): Packet {
    val version = binary.consumeInt(3)
    val type = binary.consumeInt(3)

    return if (type == 4) {
        readLiteralPacket(binary, version, type)
    } else {
        readOperatorPacket(binary, version, type)
    }
}

fun readLiteralPacket(binary: ConsumableBinary, version: Int, type: Int): LiteralPacket {
    val number = StringBuilder()
    var cont = true
    while (cont) {
        val five = binary.consume(5)
        cont = five.startsWith("1")
        number.append(five.substring(1))
    }
    return LiteralPacket(version, type, number.toString().toLong(2))
}


fun readOperatorPacket(binary: ConsumableBinary, version: Int, type: Int): OperatorPacket {
    val lengthType = binary.consumeInt(1)
    val packets: List<Packet> = if (lengthType == 0) {
        readMultiplePacketsByBits(binary, bits = binary.consumeInt(15))
    } else {
        readMultiplePacketsByCount(binary, count = binary.consumeInt(11))
    }

    return OperatorPacket(version, type, packets)
}

fun readMultiplePacketsByBits(binary: ConsumableBinary, bits: Int): List<Packet> {
    val subBinary = ConsumableBinary(binary.consume(bits))
    val packets = mutableListOf<Packet>()
    while (!subBinary.isEmpty()) {
        packets.add(readPacket(subBinary))
    }
    return packets
}

fun readMultiplePacketsByCount(binary: ConsumableBinary, count: Int): List<Packet> {
    return (0 until count).map {
        readPacket(binary)
    }
}
