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
        println("79404 is correct")
        return cheapestRoute()
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }

    private fun cheapestRoute(): Long {
        val visitedMap = mutableMapOf<Point, Reindeer>()

        val compareByCost: Comparator<Reindeer> = compareBy { it.cost }
        val priorityQueue = PriorityQueue<Reindeer>(compareByCost)

        priorityQueue.add(Reindeer(startPos, Direction.RIGHT, 0L))
        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.remove()
            visitedMap.remove(current.pos)

            if (current.pos == endPos) {
                return current.cost
            }

            Direction.entries.filter{d -> d.opposite() != current.dir }.forEach { newDir ->
                val nextPos = current.pos.moveOneStep(newDir)
                if (nextPos in legalFields) {
                    val newCost = current.cost + 1 + if (current.dir == newDir) 0L else 1000L

                    if (visitedMap.contains(nextPos)) {
                        if (visitedMap[nextPos]!!.cost > newCost) {
                            visitedMap[nextPos] = Reindeer(nextPos, newDir, newCost)
                            val alreadyInQueue = priorityQueue.firstOrNull { it.pos == nextPos }
                            priorityQueue.remove(alreadyInQueue)
                            priorityQueue.add(Reindeer(nextPos, newDir, newCost))
                        }
                    } else {
                        visitedMap[nextPos] = Reindeer(nextPos, newDir, newCost)
                        priorityQueue.add(Reindeer(nextPos, newDir, newCost))
                    }

                }
            }
        }
        return -1L
    }
}

data class Reindeer(val pos: Point, val dir: Direction, val cost: Long)


