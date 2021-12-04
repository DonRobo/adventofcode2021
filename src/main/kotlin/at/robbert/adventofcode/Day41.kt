package at.robbert.adventofcode

fun main() {
    val input = getInput(4)
    val drawnNumbers = input.first().split(",").mapNotNull { it.toIntOrNull() }
    println("Drawn numbers: ${drawnNumbers.size}")

    val bingoBoards = BingoSystem.parseBingoBoards(input.drop(1))

    drawnNumbers.forEach { number ->
        bingoBoards.forEach { bingoBoard ->
            val won = bingoBoard.drawnNumber(number)
            if (won >= 0) {
                println("Board won: $won")
                return
            }
        }
    }
}

class BingoBoard(numbers: List<List<Int>>) {
    private val numbers: IntArray
    private val marked: BooleanArray = BooleanArray(25) { false }

    fun drawnNumber(number: Int): Int {
        for (i in 0..24) {
            if (numbers[i] == number) {
                marked[i] = true
            }
        }

        return if (hasWon()) {
            val sumOfUnmarked = (0..24).sumOf { i -> if (!marked[i]) numbers[i] else 0 }
            sumOfUnmarked * number
        } else {
            -1
        }
    }

    private fun isMarked(x: Int, y: Int): Boolean {
        return marked[y * 5 + x]
    }

    fun hasWon(): Boolean {
        //horizontal
        for (y in 0..4) {
            if ((0..4).all { x -> isMarked(x, y) }) {
                return true
            }
        }

        //vertical
        for (x in 0..4) {
            if ((0..4).all { y -> isMarked(x, y) }) {
                return true
            }
        }

        //diagonal
//        if ((0..4).all { x -> isMarked(x, x) }) {
//            return true
//        }
//        if ((0..4).all { x -> isMarked(x, 4 - x) }) {
//            return true
//        }

        return false
    }

    fun debugOutput() {
        println("-----------------")
        for (y in 0..4) {
            val line = (0..4).joinToString(" ") { x ->
                val marked = if (isMarked(x, y)) "*" else " "
                marked + "%2d".format(numbers[y * 5 + x]) + marked
            }
            println(line)
        }
        println("-----------------")
    }

    init {
        this.numbers = numbers.flatten().toIntArray()
    }
}

object BingoSystem {
    fun parseBingoBoards(input: List<String>): List<BingoBoard> {
        val currentLines = mutableListOf<List<Int>>()
        val boards = mutableListOf<BingoBoard>()

        input.forEach { line ->
            val currentLine = line.split(" ").mapNotNull { it.toIntOrNull() }
            if (currentLine.isEmpty()) return@forEach

            require(currentLine.size == 5) { "Invalid line: $line" }

            currentLines.add(currentLine)
            if (currentLines.size == 5) {
                boards += BingoBoard(currentLines)
                currentLines.clear()
            }
        }

        require(currentLines.isEmpty()) { "Invalid input, some lines left: $currentLines" }

        return boards
    }
}
