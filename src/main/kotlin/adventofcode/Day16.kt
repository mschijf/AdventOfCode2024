package adventofcode

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import java.util.*

fun main() {
    Day16(test=false).showResult()
}

class Day16(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Reindeer Maze", hasInputFile = true) {

    private val maze = inputAsGrid("example")
    private val legalFields = maze.filterValues { it == '.' || it == 'S' || it == 'E'}.keys
    private val startPos = maze.filterValues { it == 'S' }.keys.first()
    private val endPos = maze.filterValues { it == 'E' }.keys.first()

    override fun resultPartOne(): Any {
//        println("79404 is correct")
        return cheapestRoute().first
    }

    override fun resultPartTwo(): Any {
//        println("451 is correct")
        return cheapestRoute().second
    }

    private fun cheapestRoute(): Pair<Int, Int> {
        var cheapestRoute = Int.MAX_VALUE
        val totalSet = mutableSetOf<Point>()

        val visitedMap = mutableMapOf<Point, Tile>()
        val visitedCombi = mutableSetOf<Pair<Point, Direction>>()

        val compareByCost: Comparator<Tile> = compareBy { it.cost }
        val priorityQueue = PriorityQueue<Tile>(compareByCost)

        priorityQueue.add(Tile(startPos, Direction.RIGHT, 0, emptyList()))
        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.remove()
            visitedMap.remove(current.pos)
            visitedCombi.add(Pair(current.pos, current.dir))
            if (current.pos == endPos) {
                cheapestRoute = current.cost
                totalSet += listOf(startPos) + current.pathToTile
            } else if (current.cost < cheapestRoute) {

                Direction.entries.filter { d -> d.opposite() != current.dir }.forEach { newDir ->
                    val nextPos = current.pos.moveOneStep(newDir)
                    if (nextPos in legalFields) {
                        val newCost = current.cost + 1 + if (current.dir == newDir) 0 else 1000
                        val nextTile = Tile(nextPos, newDir, newCost, current.pathToTile + nextPos)
                        if (visitedMap.contains(nextPos)) {
                            if (visitedMap[nextPos]!!.cost > newCost) {
                                visitedMap[nextPos] = nextTile
                                val alreadyInQueue = priorityQueue.firstOrNull { it.pos == nextPos }
                                priorityQueue.remove(alreadyInQueue)
                                priorityQueue.add(nextTile)
                            } else if (visitedMap[nextPos]!!.cost == newCost) {
                                if (Pair(nextPos, newDir) !in visitedCombi)
                                    priorityQueue.add(nextTile)
                            }
                        } else {
                            visitedMap[nextPos] = nextTile
                            priorityQueue.add(nextTile)
                        }

                    }
                }

            }
        }
        return Pair(cheapestRoute, totalSet.size)
    }
}

data class Tile(val pos: Point, val dir: Direction, val cost: Int, val pathToTile: List<Point>)


