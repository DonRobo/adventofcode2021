package at.robbert.adventofcode

fun main() {
    val input = getInput(4)
    val drawnNumbers = input.first().split(",").mapNotNull { it.toIntOrNull() }
    println("Drawn numbers: ${drawnNumbers.size}")

    var bingoBoards = BingoSystem.parseBingoBoards(input.drop(1))

    var lastWinner = -1
    drawnNumbers.forEach { number ->
        bingoBoards.forEach { bingoBoard ->
            val won = bingoBoard.drawnNumber(number)
            if (won >= 0) {
                println("Board won: $won")
                lastWinner = won
            }
        }
        bingoBoards = bingoBoards.filter { !it.hasWon() }
    }

    println("Last winner: $lastWinner")
}
