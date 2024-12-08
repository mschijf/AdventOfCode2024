package adventofcode

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point

fun main() {
    Day06(test=false).showResult()
}

class Day06(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Guard Gallivant", hasInputFile = true) {

    private val grid = inputAsGrid()
    private val obstructions = grid.filter { it.value == '#' }.keys
    private val allFields = grid.keys
    private val guardPos = grid.filter { it.value in "^<>v" }.keys.first()
    private val guardDir = Direction.ofSymbol(grid[guardPos]!!.toString())

    private val maxX = allFields.maxOf{ it.x }
    private val maxY = allFields.maxOf{ it.y }

    override fun resultPartOne(): Any {
        return doTheWalk()
    }

    override fun resultPartTwo(): Any {
        val emptySpots = allFields-obstructions-guardPos
        return emptySpots.count { emptySpot -> hasCyclicWalk(emptySpot) }
    }

    private fun doTheWalk(): Int {
        val visited = mutableSetOf<Point>(guardPos)
        var currentPos = guardPos
        var currentDirection = guardDir
        while (currentPos.onGrid()) {
            if (currentPos.moveOneStep(currentDirection) in obstructions) {
                currentDirection = currentDirection.rotateRight()
            } else {
                currentPos = currentPos.moveOneStep(currentDirection)
                visited += currentPos
            }
        }
        return (visited.intersect(allFields)).size
    }

    private fun hasCyclicWalk(extraObstruction: Point): Boolean {
        val visited = mutableSetOf<Pair<Point, Direction>>()
        var currentPos = guardPos
        var currentDirection = guardDir
        do  {
            visited += Pair(currentPos, currentDirection)

            val possibleNextPos = currentPos.moveOneStep(currentDirection)
            if (possibleNextPos in obstructions || possibleNextPos == extraObstruction) {
                currentDirection = currentDirection.rotateRight()
            } else {
                currentPos = possibleNextPos
            }
            if (Pair(currentPos, currentDirection) in visited) {
                return true
            }
        } while (currentPos.onGrid())
        return false
    }

    private fun Point.onGrid(): Boolean {
        return this.x in 0..maxX && this.y in 0..maxY
    }
}


