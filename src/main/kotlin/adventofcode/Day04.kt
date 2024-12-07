package adventofcode

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.WindDirection

fun main() {
    Day04(test=false).showResult()
}

class Day04(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Ceres Search", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val searchPuzzle = inputAsGrid()
        val startPoints = searchPuzzle.filter { it.value == 'X' }.keys
        return startPoints.sumOf { searchPuzzle.checkAllDirections(it) }
    }

    override fun resultPartTwo(): Any {
        val searchPuzzle = inputAsGrid()
        val startPoints = searchPuzzle.filter { it.value == 'A' }.keys
        return startPoints.count { searchPuzzle.checkX(it) }
    }

    private fun Map<Point, Char>.checkAllDirections(startPoint: Point): Int {
        return WindDirection.entries.count { dir -> this.checkDirection(startPoint, dir) }
    }

    private fun Map<Point, Char>.checkDirection(startPoint: Point, direction: WindDirection): Boolean {
        var word = ""
        var currentPoint = startPoint
        while (currentPoint  in this.keys) {
            word += this.getValue(currentPoint)
            if (word == "XMAS") {
                return true
            }
            currentPoint = currentPoint.moveOneStep(direction)
        }
        return false
    }

    private fun Map<Point, Char>.checkX(startPoint: Point): Boolean {
        return this.checkDiagonal(startPoint, WindDirection.NORTHEAST) && this.checkDiagonal(startPoint, WindDirection.NORTHWEST)
    }

    private fun Map<Point, Char>.checkDiagonal(startPoint: Point, direction: WindDirection): Boolean {
        if ( (startPoint.moveOneStep(direction) in this.keys) && (startPoint.moveOneStep(direction.opposite()) in this.keys) ) {
            val word = "" +
                    this.getValue(startPoint.moveOneStep(direction)) +
                    this.getValue(startPoint) +
                    this.getValue(startPoint.moveOneStep(direction.opposite()))
            return (word == "SAM" || word == "MAS")
        }
        return false
    }


}


