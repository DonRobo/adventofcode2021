package at.robbert.adventofcode

import kotlin.system.exitProcess

sealed interface Operand

data class Literal(val value: Long) : Operand
data class Variable(val name: String) : Operand

sealed interface Instruction

data class Input(val target: Variable) : Instruction
data class Add(val op1: Variable, val op2: Operand) : Instruction
data class Mul(val op1: Variable, val op2: Operand) : Instruction
data class Div(val op1: Variable, val op2: Operand) : Instruction
data class Mod(val op1: Variable, val op2: Operand) : Instruction
data class Eql(val op1: Variable, val op2: Operand) : Instruction

class ALU(private val program: List<Instruction>) {

    fun run(input: List<Long>): Map<String, Long> {
        require(program.count { it is Input } == input.size) {
            "Input size must match number of inputs\nFound ${input.size} inputs, but program expects ${program.count { it is Input }}"
        }

        val variables = mutableMapOf<String, Long>()
        fun read(variable: Operand): Long {
            return when (variable) {
                is Variable -> variables.getOrDefault(variable.name, 0L)
                is Literal -> variable.value
            }
        }

        fun write(variable: Variable, value: Long) {
            variables[variable.name] = value
        }

        var inputIndex = 0
        fun readInput(): Long {
            require(inputIndex < input.size)
            return input[inputIndex++]
        }

        program.forEach { instruction ->
            when (instruction) {
                is Input -> write(instruction.target, readInput())
                is Add -> write(instruction.op1, read(instruction.op1) + read(instruction.op2))
                is Div -> write(instruction.op1, read(instruction.op1) / read(instruction.op2))
                is Eql -> write(instruction.op1, if (read(instruction.op1) == read(instruction.op2)) 1 else 0)
                is Mod -> write(instruction.op1, read(instruction.op1) % read(instruction.op2))
                is Mul -> write(instruction.op1, read(instruction.op1) * read(instruction.op2))
            }
        }

        return variables
    }
}

fun parseProgram(lines: List<String>): List<Instruction> {
    val r = Regex("(inp|add|mul|div|mod|eql) ([-\\w]+)(?: ([-\\w]+))?")
    return lines.map { line ->
        val matched = r.matchEntire(line)

        requireNotNull(matched)

        val (operation, op1Str, op2Str) = matched.destructured

        val op1 = op1Str.toLongOrNull()?.let { Literal(it) } ?: Variable(op1Str)
        val op2 = op2Str.toLongOrNull()?.let { Literal(it) } ?: Variable(op2Str)

        require(op1 is Variable)

        when (operation) {
            "inp" -> Input(op1)
            "add" -> Add(op1, op2)
            "mul" -> Mul(op1, op2)
            "div" -> Div(op1, op2)
            "mod" -> Mod(op1, op2)
            "eql" -> Eql(op1, op2)
            else -> throw IllegalArgumentException("Unknown operation $operation")
        }
    }
}

fun main() {
    val alu = ALU(parseProgram(getInput(24, example = false).filterNot { it.isBlank() }))

    val number = Array(14) { 1 }.toMutableList()
    while (true) {
        val result = alu.run(number.map { it.toLong() })
        if (result["z"] == 0L) {
            println("Number: " + number.joinToString(""))
            println(result)
        } else if (number.slice(7..13).all { it == 1 }) {
            println("Running ${number.joinToString("")}")
        }

        number[13]++
        while (number.any { it > 9 }) {
            val index = number.indexOfLast { it > 9 }
            if (index == 0) {
                println("Done")
                exitProcess(0)
            }

            number[index - 1]++
            number[index] = 1
        }
    }
}
