package adventofcode

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.real.Line
import tool.mylambdas.splitByCondition
import tool.primarytype.isCloseToLong
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

    override fun resultPartTwo(): Any {
        val clawMachineList = inputLines.splitByCondition { it.isBlank() }.map{ ClawMachine.of(it)}
        return clawMachineList.sumOf{it.calculateByIntersectionPoint(costA, costB, extra=10000000000000)}
    }

    private fun ClawMachine.calculateCosts(): Int {
        val grid = (0..maxPushes).flatMap { a ->
            (0..maxPushes).map { b->
                Pair (a*costA + b*costB, a*this.buttonA + b*this.buttonB)
            }
        }
        return grid.filter { it.second == this.prize }.minOfOrNull { it.first } ?:0
    }

    operator fun Point.plus(other: Point): Point = this.plusXY(other.x, other.y)
    operator fun Int.times(other: Point): Point = pos(this*other.x, this*other.y)
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
            return pos(
                x = this.substringAfter("X+").substringBefore(",").trim().toInt(),
                y = this.substringAfter("Y+").trim().toInt()
            )
        }

        private fun String.prizeToPoint(): Point {
            return pos(
                x = this.substringAfter("X=").substringBefore(",").trim().toInt(),
                y = this.substringAfter("Y=").trim().toInt()
            )
        }
    }

    fun calculateByIntersectionPoint(costA: Int, costB: Int, extra: Long): Long {
        val line1 = Line(buttonA.x.toDouble(), buttonB.x.toDouble(), prize.x.toDouble()+extra)
        val line2 = Line(buttonA.y.toDouble(), buttonB.y.toDouble(), prize.y.toDouble()+extra)
        val (aPushes, bPushes) = line1.intersectionOrNull(line2)?: return 0

        return if (aPushes.isCloseToLong() && bPushes.isCloseToLong()) {
            aPushes.roundToLong()*costA + bPushes.roundToLong() * costB
        } else {
            0
        }
    }

}


