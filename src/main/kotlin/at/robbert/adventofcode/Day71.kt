package at.robbert.adventofcode

import com.google.ortools.Loader
import com.google.ortools.sat.CpModel
import com.google.ortools.sat.CpSolver
import com.google.ortools.sat.CpSolverStatus
import com.google.ortools.sat.LinearExpr

fun main() {
    Loader.loadNativeLibraries()

    val input = getInput(7, example = false)
        .single { it.isNotBlank() }
        .split(",")
        .mapNotNull { it.toIntOrNull() }

    val min = input.minOrNull()!!
    val max = input.maxOrNull()!!

    val model = CpModel()
    val crabs = List(input.size) { index ->
        model.newIntVar(min.toLong(), max.toLong(), "crab$index")!!
    }
    val crabFuel = crabs.mapIndexed { index, crab ->
        val v = model.newIntVar((min - max).toLong(), (max - min).toLong(), "crabFuel$index")!!
        val vAbs = model.newIntVar(0, (max - min).toLong(), "crabFuelAbs$index")!!
        model.addAbsEquality(vAbs, v)
        model.addLinearConstraint(LinearExpr.sum(arrayOf(crab, v)), input[index].toLong(), input[index].toLong())
        vAbs
    }
    val fuelUsed = model.newIntVar(0, (max - min) * input.size.toLong(), "fuelUsed")!!
    model.addLinearConstraint(
        LinearExpr.scalProd(
            (crabFuel + fuelUsed).toTypedArray(),
            (crabFuel.map { 1L } + listOf(-1L)).toLongArray()
        ),
        0, 0
    )
    (1 until crabs.size).forEach { i ->
        model.addEquality(crabs[0], crabs[i])
    }
    model.minimize(fuelUsed)

    val solver = CpSolver()
    val solved = solver.solve(model)
    require(solved == CpSolverStatus.OPTIMAL) { "Could not solve" }

    println("Old crab positions:")
    println(input.joinToString(", ") { it.toString() })
    println("New crab positions:")
    println(crabs.joinToString(", ") { c ->
        "${solver.value(c)}"
    })
    println("Crab fuels:")
    println(crabFuel.joinToString(", ") { c ->
        "${solver.value(c)}"
    })

    println("Fuel used: ${solver.value(fuelUsed)}")
}
