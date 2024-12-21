package adventofcode

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.mylambdas.collectioncombination.filterCombinedItems

fun main() {
    Day20(test=false).showResult()
}

class Day20(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Race Condition", hasInputFile = true) {

    private val raceTrack = inputAsGrid()
    private val trackSet = raceTrack.filterValues { it == '.'  || it == 'S' || it == 'E'}.keys
    private val start = raceTrack.filterValues { it == 'S'}.keys.first()
    private val end = raceTrack.filterValues { it == 'E'}.keys.first()
    private val walls = raceTrack.filterValues { it == '#'}.keys
    private val maxX = walls.maxOf { it.x }
    private val maxY = walls.maxOf { it.y }

    private val shortestPathFromStartPointMap = determineAllPoints(start)
    private val shortestPathToEndPointMap = determineAllPoints(end)

    override fun resultPartOne(): Any {
        val insideWalls = walls.filter { it.x != 0 && it.y != 0 && it.x != maxX && it.y != maxY }

        val cheatCandidates = insideWalls.flatMap { wall ->
            Direction.entries
                .filter { dir -> wall.moveOneStep(dir) in trackSet && wall.moveOneStep(dir.opposite()) in trackSet }
                .map { dir -> Pair(wall.moveOneStep(dir.opposite()), wall.moveOneStep(dir))}
        }

        val normalTime = shortestPathFromStartPointMap[end]!!
        val x = cheatCandidates.map { cheatPair -> trackSet.shortestRoute(cheatPair) }.filter { it < normalTime }
        println("1521 is correct")
//        return x.groupingBy { normalTime - it }.eachCount().toSortedMap()
        return x.filter {normalTime - it >= 100}.size
    }

    override fun resultPartTwo(): Any {
        val minimalToSave = if (test) 50 else 100
        val cheatCandidates = trackSet.toList()
            .filterCombinedItems { point1, point2 ->  point1.distanceTo(point2) <= 20 }
        val normalTime = shortestPathFromStartPointMap[end]!!
        println(normalTime)

        val xx = cheatCandidates.flatMap { cheatPair ->
            listOf(trackSet.shortestRoute2(cheatPair), trackSet.shortestRoute2(Pair(cheatPair.second, cheatPair.first)))}

        println("988903 is too low")
//        return xx.filter{normalTime - it >= minimalToSave}.groupingBy { normalTime - it }.eachCount().toSortedMap()
        return xx.count {normalTime - it >= minimalToSave}
    }

    private fun Set<Point>.shortestRoute(cheatCandidate: Pair<Point, Point>): Int {
        return 2 +
                shortestPathFromStartPointMap[cheatCandidate.first]!! +
                shortestPathToEndPointMap[cheatCandidate.second]!!
    }

    private fun Set<Point>.shortestRoute2(cheatCandidate: Pair<Point, Point>): Int {
        return cheatCandidate.first.distanceTo(cheatCandidate.second) +
                shortestPathFromStartPointMap[cheatCandidate.first]!! +
                shortestPathToEndPointMap[cheatCandidate.second]!!
    }


    private fun determineAllPoints(startPoint: Point): Map<Point, Int> {
        val shortestFromPoint = mutableMapOf<Point, Int>()
        val queue = ArrayDeque<Pair<Point, Int>>()
        queue.add(Pair(startPoint, 0))
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            shortestFromPoint[current.first] = current.second
            current.first.neighbors().filter { it in trackSet && it !in shortestFromPoint }.forEach { nb ->
                queue.add(Pair(nb, current.second+1))
            }
        }
        return shortestFromPoint
    }
}
