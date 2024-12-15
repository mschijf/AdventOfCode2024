package adventofcode

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.twodimensional.printAsGrid
import tool.mylambdas.splitByCondition

fun main() {
    Day15(test=false).showResult()
}

class Day15(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Warehouse Woes", hasInputFile = true) {

    private val input = inputLines.splitByCondition { it.isBlank() }
    private val moveList = input.last().joinToString("").toList()

    override fun resultPartOne(): Any {
        val grid = Grid.of(input.first().asGrid())
        moveList.forEach { mv -> grid.moveRobot(mv) }
//        grid.print()
        return grid.gpsSum()
    }

    override fun resultPartTwo(): Any {
        val grid = WideGrid.of(input.first().asGrid())
        moveList.forEach { mv -> grid.moveRobot(mv) }
//        grid.print()
        return grid.gpsSum()
    }

}

//======================================================================================================================

class Grid(initialWarehousMap: Map<Point, Char>, initialRobotPos: Point) {
    private val grid = initialWarehousMap.toMutableMap()
    private var robotPos = initialRobotPos

    fun moveRobot(aMove: Char) {
        val dir = Direction.ofSymbol(aMove.toString())
        val nextPos = robotPos.moveOneStep(dir)
        if (grid[nextPos] == 'O') {
            val firstEmpty = firtsEmptySpotOrNull(nextPos, dir)
            if (firstEmpty != null) {
                grid[nextPos] = '.'
                grid[firstEmpty] = 'O'
                robotPos = nextPos
            }
        } else if (grid[nextPos] != '#') {
            robotPos = nextPos
        }
    }

    private fun firtsEmptySpotOrNull(fromPos: Point, dir: Direction): Point? {
        var current = fromPos
        while (grid[current] == 'O') {
            current = current.moveOneStep(dir)
        }
        return if (grid[current] == '#') null else current
    }

    fun gpsSum(): Int {
        return grid.filterValues { it == 'O' }.keys.sumOf {it.x + 100*it.y}
    }

    fun print() {
        (grid+ mapOf(robotPos  to '@')).printAsGrid { ch -> ch.toString() }
    }

    companion object {
        fun of(rawGrid: Map<Point,Char>): Grid {
            val robotPos = rawGrid.filterValues { it == '@'}.keys.first()
            return Grid(
                initialWarehousMap = rawGrid.filterValues { it != '@' } + mapOf(robotPos  to '.') ,
                initialRobotPos = robotPos
            )
        }
    }
}

//======================================================================================================================

class WideGrid(initialWarehousMap: Map<Point, Char>, initialRobotPos: Point) {

    private val grid = initialWarehousMap.toMutableMap()
    private var robotPos = initialRobotPos

    fun moveRobot(aMove: Char) {
        val dir = Direction.ofSymbol(aMove.toString())
        val nextPos = robotPos.moveOneStep(dir)
        if (grid[nextPos] == '[' || grid[nextPos] == ']' ) {
            if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                val firstEmpty = firstEmptySpotOrNullHorizontal(nextPos, dir)
                if (firstEmpty != null) {
                    moveBoxesHorizontally(nextPos, firstEmpty, dir)
                    robotPos = nextPos
                }
            } else {
                val boxesToMove = boxesToMoveVertically(nextPos, dir)
                if (boxesToMove.isNotEmpty()) {
                    moveBoxesVertically(boxesToMove, dir)
                    robotPos = nextPos
                }
            }
        } else if (grid[nextPos] != '#') {
            robotPos = nextPos
        }
    }

    private fun firstEmptySpotOrNullHorizontal(fromPos: Point, dir: Direction): Point? {
        var current = fromPos
        while (grid[current] == '[' || grid[current] == ']') {
            current = current.moveOneStep(dir)
        }
        return if (grid[current] == '#') null else current
    }

    private fun moveBoxesHorizontally(firstBoxPos: Point, toEmptySpot: Point, dir: Direction) {
        var current = toEmptySpot
        while (current != firstBoxPos) {
            val nextNeighbour = current.moveOneStep(dir.opposite())
            grid[current] = grid[nextNeighbour]!!
            grid[nextNeighbour] = '.'
            current = nextNeighbour
        }
    }


    private fun boxesToMoveVertically(fromPos: Point, dir: Direction): Set<Point> {
        var currentLevelBoxes = if (grid[fromPos] == '[') {
            listOf(fromPos, fromPos.right())
        } else {
            listOf(fromPos, fromPos.left())
        }

        val allBoxes = mutableSetOf<Point>()
        while (true) {
            allBoxes.addAll(currentLevelBoxes)
            val nextLevelBoxes =
                currentLevelBoxes.map { it.moveOneStep(dir) }.filter { grid[it] != '.' }.sortedBy { it.x }
                    .toMutableList()
            if (nextLevelBoxes.any { grid[it] == '#' })
                return emptySet()
            if (nextLevelBoxes.isEmpty())
                return allBoxes

            val extraSet = mutableSetOf<Point>()
            nextLevelBoxes.forEach { bp ->
                if (grid[bp] == '[') {
                    extraSet.add (bp.right())
                } else {
                    extraSet.add (bp.left())
                }
            }
            currentLevelBoxes = nextLevelBoxes + extraSet
        }
    }

    private fun moveBoxesVertically(boxesToMove: Set<Point>, dir: Direction) {
        val bb = if (dir == Direction.UP)
            boxesToMove.sortedBy { it.y }
        else
            boxesToMove.sortedByDescending { it.y }

        bb.forEach { boxPos ->
            grid[boxPos.moveOneStep(dir)] = grid[boxPos]!!
            grid[boxPos] = '.'
        }
    }

    fun gpsSum(): Int {
        return grid.filterValues { it == '[' }.keys.sumOf {it.x + 100*it.y}
    }

    fun print() {
        (grid+ mapOf(robotPos  to '@')).printAsGrid { ch -> ch.toString() }
    }

    companion object {
        fun of(rawGrid: Map<Point,Char>): WideGrid {
            val doubleGrid = rawGrid.toDoubleGrid()
            val robotPos = doubleGrid.filterValues { it == '@'}.keys.first()
            return WideGrid(
                initialWarehousMap = doubleGrid.filterValues { it != '@' } + mapOf(robotPos to '.') ,
                initialRobotPos = robotPos
            )
        }

        private fun Map<Point, Char>.toDoubleGrid(): MutableMap<Point, Char> {
            return this.map {
                val ch = it.value
                val leftPos = pos(it.key.x * 2, it.key.y)
                val rightPos = pos(it.key.x * 2 + 1, it.key.y)
                when (ch) {
                    '@' -> listOf(Pair(leftPos, ch), Pair(rightPos, '.'))
                    '#' -> listOf(Pair(leftPos, ch), Pair(rightPos, '#'))
                    'O' -> listOf(Pair(leftPos, '['), Pair(rightPos, ']'))
                    else -> listOf(Pair(leftPos, ch), Pair(rightPos, ch))
                }
            }.flatten().toMap().toMutableMap()
        }
    }
}
