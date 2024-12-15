package adventofcode

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.twodimensional.printAsGrid
import tool.mylambdas.splitByCondition

fun main() {
    Day15(test=false).showResult()
}


// 1515655 too high

class Day15(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Warehouse Woes", hasInputFile = true) {

    private val input = inputLines.splitByCondition { it.isBlank() }
    val moveList = input.last().joinToString("").toList()

//    val grid = input.first().asGrid()
//    var robotPos = grid.filterValues { it == '@'}.keys.first()
//    val walls = grid.filterValues { it == '#'}.keys
//    val boxes = grid.filterValues { it == 'O'}.keys.toMutableSet()
//
//    override fun resultPartOne(): Any {
//        moveList.forEach { mv ->
//            move(mv)
//        }
//        printGrid()
//        return boxes.sumOf {it.x + 100*it.y}
//    }
//
//    private fun printGrid() {
//        val newGrid = walls.map{ it to '#'}.toMap() + boxes.map{ it to '0'}.toMap() + (robotPos to '@')
//        newGrid.printAsGrid { ch -> ch.toString() }
//    }
//
//    private fun move(aMove: Char) {
//        val dir = Direction.ofSymbol(aMove.toString())
//        val nextPos = robotPos.moveOneStep(dir)
//        if (nextPos in boxes) {
//            val firstEmpty = firtsEmptySpotOrNull(nextPos, dir)
//            if (firstEmpty != null) {
//                boxes.remove(nextPos)
//                boxes.add(firstEmpty)
//                robotPos = nextPos
//            }
//        } else if (nextPos !in walls) {
//            robotPos = nextPos
//        }
//    }
//
//    private fun firtsEmptySpotOrNull(fromPos: Point, dir: Direction): Point? {
//        var current = fromPos
//        while (current in boxes) {
//            current = current.moveOneStep(dir)
//        }
//        return if (current in walls) null else current
//    }

    //------------------------------------------------------------------------------------------------------------------

    private fun List<String>.toDoubleGrid(): Map<Point, Char> {
        return this
            .flatMapIndexed { y, line ->
                line.flatMapIndexed { x, ch ->
                    when (ch) {
                        '@' -> listOf(Pair(pos(2*x,y), ch), Pair(pos(2*x+1,y), '.'))
                        '#' -> listOf(Pair(pos(2*x,y), ch), Pair(pos(2*x+1,y), ch))
                        'O' -> listOf(Pair(pos(2*x,y), '['), Pair(pos(2*x+1,y), ']'))
                        else -> listOf(Pair(pos(2*x,y), ch), Pair(pos(2*x+1,y), ch))
                    }
                }
            }.toMap()
    }

    val bigGrid = input.first().toDoubleGrid().toMutableMap()
    var bigRobotPos = bigGrid.filterValues { it == '@'}.keys.first()

    override fun resultPartTwo(): Any {
        bigGrid.printAsGrid { ch -> ch.toString() }
        println()
        println()
        moveList.forEach { mv -> bigMove(mv) }
        bigGrid.printAsGrid { ch -> ch.toString() }
        println()
        println("wrong attempt: 1515655 too high")
        return bigGrid.filterValues { it == '[' }.keys.sumOf {it.x + 100*it.y}
    }

    private fun bigMove(aMove: Char) {
        val dir = Direction.ofSymbol(aMove.toString())
        val nextPos = bigRobotPos.moveOneStep(dir)
        if (bigGrid[nextPos] == '[' || bigGrid[nextPos] == ']' ) {
            if (dir == Direction.LEFT || dir == Direction.RIGHT) {
                val firstEmpty = firstEmptySpotOrNullHorizontal(nextPos, dir)
                if (firstEmpty != null) {
                    var current = firstEmpty!!
                    while (current != nextPos) {
                        val nextNeighbour = current.moveOneStep(dir.opposite())
                        bigGrid[current] = bigGrid[nextNeighbour]!!
                        bigGrid[nextNeighbour] = '.'
                        current = nextNeighbour
                    }
                    bigGrid[nextPos] = '@'
                    bigGrid[bigRobotPos] = '.'
                    bigRobotPos = nextPos
                }
            } else {
                val boxesToMove = boxesToMoveVertically(nextPos, dir)
                if (boxesToMove.isNotEmpty()) {
                    moveBoxesVertically(boxesToMove, dir)
                    bigGrid[nextPos] = '@'
                    bigGrid[bigRobotPos] = '.'
                    bigRobotPos = nextPos
                }
            }
        } else if (bigGrid[nextPos] != '#') {
            bigGrid[nextPos] = '@'
            bigGrid[bigRobotPos] = '.'
            bigRobotPos = nextPos
        }
    }

    private fun firstEmptySpotOrNullHorizontal(fromPos: Point, dir: Direction): Point? {
        var current = fromPos
        while (bigGrid[current] == '[' || bigGrid[current] == ']') {
            current = current.moveOneStep(dir)
        }
        return if (bigGrid[current] == '#') null else current
    }

    private fun boxesToMoveVertically(fromPos: Point, dir: Direction): Set<Point> {
        var allBoxes = mutableSetOf<Point>()

        var currentLevelBoxes = if (bigGrid[fromPos] == '[') {
            listOf(fromPos, fromPos.right())
        } else {
            listOf(fromPos, fromPos.left())
        }

        while (true) {
            allBoxes.addAll(currentLevelBoxes)
            val nextLevelBoxes =
                currentLevelBoxes.map { it.moveOneStep(dir) }.filter { bigGrid[it] != '.' }.sortedBy { it.x }
                    .toMutableList()
            if (nextLevelBoxes.any { bigGrid[it] == '#' })
                return emptySet()
            if (nextLevelBoxes.isEmpty())
                return allBoxes
            val mostLeft = nextLevelBoxes.first()
            val mostRight = nextLevelBoxes.last()
            if (bigGrid[mostLeft] == ']') {
                nextLevelBoxes.add(mostLeft.left())
            }
            if (bigGrid[mostRight] == '[') {
                nextLevelBoxes.add(mostRight.right())
            }
            currentLevelBoxes = nextLevelBoxes
        }
    }

    private fun moveBoxesVertically(boxesToMove: Set<Point>, dir: Direction) {
        val bb = if (dir == Direction.UP)
            boxesToMove.sortedBy { it.y }
        else
            boxesToMove.sortedByDescending { it.y }

        bb.forEach { boxPos ->
            bigGrid[boxPos.moveOneStep(dir)] = bigGrid[boxPos]!!
            bigGrid[boxPos] = '.'
        }
    }

}


