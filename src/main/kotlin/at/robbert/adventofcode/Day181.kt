package at.robbert.adventofcode

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

sealed interface SnailfishNumber {
    fun toPrettyString(): String
    fun splitIfNeeded(): SnailfishNumberPair?

    val magnitude: Long
}

data class SimpleSnailfishNumber(val value: Int) : SnailfishNumber {
    override fun toPrettyString(): String {
        return value.toString()
    }

    override fun splitIfNeeded(): SnailfishNumberPair? {
        return if (value >= 10) {
            SnailfishNumberPair(
                SimpleSnailfishNumber(floor(value / 2.0).roundToInt()),
                SimpleSnailfishNumber(ceil(value / 2.0).roundToInt())
            )
        } else {
            null
        }
    }

    override val magnitude: Long
        get() = value.toLong()
}

data class SnailfishNumberPair(val left: SnailfishNumber, val right: SnailfishNumber) : SnailfishNumber {
    override fun toPrettyString(): String {
        return "[${left.toPrettyString()},${right.toPrettyString()}]"
    }

    operator fun plus(other: SnailfishNumberPair): SnailfishNumberPair {
        return SnailfishNumberPair(this, other).also { println("After addition: ${it.toPrettyString()}") }.reduced()
            .also { println("After reduction: ${it.toPrettyString()}") }
    }

    private fun reduced(): SnailfishNumberPair =
        explodeIfNeeded()?.reduced() ?: (splitIfNeeded()?.reduced() ?: this)

    override fun splitIfNeeded(): SnailfishNumberPair? {
        left.splitIfNeeded()?.let {
            return SnailfishNumberPair(it, right).also {
                println("Reduced\n${this.toPrettyString()} to\n${it.toPrettyString()}")
            }
        }
        right.splitIfNeeded()?.let {
            return SnailfishNumberPair(left, it).also {
                println("Reduced\n${this.toPrettyString()} to\n${it.toPrettyString()}")
            }
        }

        return null
    }

    override val magnitude: Long
        get() = 3L * left.magnitude + 2L * right.magnitude

    private fun explodeIfNeeded(): SnailfishNumberPair? {
        val numberStr = StringBuilder(toPrettyString())

        fun StringBuilder.replaceFirst(
            fromIndex: Int,
            toReplace: Regex,
            block: (String) -> String
        ): Boolean {
            val match = toReplace.find(this, fromIndex)
            return if (match != null) {
                val start = match.range.first
                val end = match.range.last
                val replacement = block(match.value)
                this.replace(start, end + 1, replacement)

                true
            } else {
                false
            }
        }

        fun StringBuilder.replaceLast(upToIndexExcl: Int, toReplace: Regex, block: (String) -> String): Boolean {
            toReplace.findAll(this).lastOrNull { it.range.last < upToIndexExcl }?.let {
                val start = it.range.first
                val end = it.range.last
                val replacement = block(it.value)
                this.replace(start, end + 1, replacement)
                return true
            }

            return false
        }

        var depth = 0
        var index = 0
        while (depth != 5 && index < numberStr.length) {
            if (numberStr[index] == '[') {
                depth++
            } else if (numberStr[index] == ']') {
                depth--
            }
            index++
        }

        return if (depth == 5) {
            index--

            val cons = ConsumableSnailfishNumberString(numberStr.substring(index))
            val pair = cons.consumePair()
            numberStr.replaceFirst(index + cons.index, Regex("\\d+")) { toBeReplaced ->
                (toBeReplaced.toInt() + (pair.right as SimpleSnailfishNumber).value).toString()
            }
            numberStr.replace(index, index + cons.index, "0")
            numberStr.replaceLast(index, Regex("\\d+")) { toBeReplaced ->
                (toBeReplaced.toInt() + (pair.left as SimpleSnailfishNumber).value).toString()
            }

            ConsumableSnailfishNumberString(numberStr.toString()).consumePair().also {
                println("Exploded\n${toPrettyString()} to\n${it.toPrettyString()}")
            }
        } else {
            null
        }
    }

}

fun main() {
    val input = getInput(18, example = false).filterNot { it.isBlank() }.map {
        ConsumableSnailfishNumberString(it).consumePair()
    }
    input.forEach {
        println(it.toPrettyString())
    }

    val sum = input.reduce { a, b ->
        a + b
    }

    println()
    println("Sum: ${sum.toPrettyString()}")
    println("Magnitude: ${sum.magnitude}")
}

class ConsumableSnailfishNumberString(private val string: String) {
    enum class Token {
        SIMPLE, PAIR
    }

    var index = 0
        private set

    fun consumeSingle(): Char {
        val c = string[index]
        index++
        return c
    }

    fun consume(length: Int): String {
        require(length <= string.length - index) {
            "Cannot consume $length characters from $string starting at $index"
        }

        val result = string.substring(index, index + length)
        index += length
        return result
    }

    fun consumeSimple(): SimpleSnailfishNumber {
        require(peek() == Token.SIMPLE)

        var length = 0
        while (index + length < string.length && string[index + length].isDigit()) {
            length++
        }

        return SimpleSnailfishNumber(consume(length).toInt())
    }

    fun isEmpty(): Boolean {
        return index >= string.length
    }

    fun peek(): Token {
        require(!isEmpty())

        val nextChar = string[index]
        return when {
            nextChar.isDigit() -> Token.SIMPLE
            nextChar == '[' -> Token.PAIR
            else -> error("Snailfish number in weird state: \"${string.substring(index)}\"")
        }
    }

    fun consumePair(): SnailfishNumberPair {
        require(peek() == Token.PAIR)
        require(consumeSingle() == '[')
        val first = consumeSnailfishNumber()
        require(consumeSingle() == ',')
        val second = consumeSnailfishNumber()
        require(consumeSingle() == ']')

        return SnailfishNumberPair(first, second)
    }

    fun consumeSnailfishNumber(): SnailfishNumber {
        return when (peek()) {
            Token.SIMPLE -> consumeSimple()
            Token.PAIR -> consumePair()
        }
    }
}
