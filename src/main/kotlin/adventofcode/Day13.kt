package adventofcode

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.xyCoordinate
import tool.mylambdas.splitByCondition
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

fun main() {
    Day13(test=false).showResult()
}

class Day13(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Claw Contraption", hasInputFile = true) {

    private val maxPushes = 100
    private val costA = 3
    private val costB = 1

    override fun resultPartOne(): Any {
        val clawMachineList = inputLines.splitByCondition { it.isBlank() }.map{ ClawMachine.of(it)}
        return clawMachineList.sumOf{it.calculateCosts()}
    }

    //11 en 12
    override fun resultPartTwo(): Any {
//        clawMachineList[11].calculateCosts()
//        return clawMachineList[11].calculateByIntersectionPoint(costA, costB)
        val clawMachineList = inputLines.splitByCondition { it.isBlank() }.map{ ClawMachine.of(it)}
        return clawMachineList.sumOf{it.calculateByIntersectionPoint(costA, costB, extra=10000000000000)}
    }

    private fun ClawMachine.calculateCosts(): Int {
        val grid = (0..maxPushes).flatMap { a ->
            (0..maxPushes).map { b->
                Pair (a*costA + b*costB, a*this.buttonA + b*this.buttonB)
            }
        }
//        println(grid.filter { it.second == this.prize }.minByOrNull { it.first } )
        return grid.filter { it.second == this.prize }.minOfOrNull { it.first } ?:0
    }

    operator fun Point.plus(other: Point): Point = this.plusXY(other.x, other.y)
    operator fun Int.times(other: Point): Point = xyCoordinate(this*other.x, this*other.y)
}

data class ClawMachine(val buttonA: Point, val buttonB: Point, val prize: Point) {

    companion object {
        fun of(input: List<String>): ClawMachine {
            return ClawMachine(
                buttonA = input[0].buttonToPoint(),
                buttonB = input[1].buttonToPoint(),
                prize = input[2].prizeToPoint()
            )
        }

        private fun String.buttonToPoint(): Point {
            return xyCoordinate(
                x = this.substringAfter("X+").substringBefore(",").trim().toInt(),
                y = this.substringAfter("Y+").trim().toInt()
            )
        }

        private fun String.prizeToPoint(): Point {
            return xyCoordinate(
                x = this.substringAfter("X=").substringBefore(",").trim().toInt(),
                y = this.substringAfter("Y=").trim().toInt()
            )
        }
    }

    fun calculateByIntersectionPoint(costA: Int, costB: Int, extra: Long): Long {
        val ax = this.buttonA.x.toDouble()
        val ay = this.buttonA.y.toDouble()
        val bx = this.buttonB.x.toDouble()
        val by = this.buttonB.y.toDouble()
        val px = this.prize.x.toDouble()+extra
        val py = this.prize.y.toDouble()+extra

        val bPushes = ( px - py*(ax/ay) ) / ( bx - by*(ax/ay) )
        val aPushes = ( px - bPushes*bx ) / ax

//        println("$aPushes  $bPushes")
        return if (aPushes.isCloseToLong() && bPushes.isCloseToLong()) {
            aPushes.roundToLong()*costA + bPushes.roundToLong() * costB
        } else {
            0
        }
    }

    private fun Double.isCloseToLong(tolerance: Double = 0.0001): Boolean {
        val nearestLong = this.roundToLong()
        return (this - nearestLong).absoluteValue <= tolerance
    }

}

