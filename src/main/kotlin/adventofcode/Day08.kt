package adventofcode

import tool.coordinate.twodimensional.Point
import tool.mylambdas.collectioncombination.toCombinedItemsList

fun main() {
    Day08(test=false).showResult()
}

class Day08(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Resonant Collinearity", hasInputFile = true) {

    private val gridFields = inputAsGrid().keys
    private val antennas = inputAsGrid().filter { it.value != '.' }
    private val antennaGroupMap = antennas.entries
        .groupBy{ entry -> entry.value }
        .mapValues { groupedEntry -> groupedEntry.value.map { it.key } }

    override fun resultPartOne(): Any {
        val allAntinodeLocations = antennaGroupMap.values.flatMap { it.determineAntinodeLocations() }.toSet()
        return allAntinodeLocations.size
    }

    override fun resultPartTwo(): Any {
        val allAntinodeLocations = antennaGroupMap.values.flatMap { it.determineAntinodeLocations2() }.toSet()
        return allAntinodeLocations.size
    }

    private fun List<Point>.determineAntinodeLocations(): Set<Point> {
        val x =  this.toCombinedItemsList().flatMap{oneStepExtension(it.first, it.second)}.toSet()
        return x
    }

    private fun List<Point>.determineAntinodeLocations2(): Set<Point> {
        val x =  this.toCombinedItemsList().flatMap{lineExtension(it.first, it.second) }.toSet()
        return x
    }


    private fun oneStepExtension(p1: Point, p2: Point): Set<Point> {
        val deltaX = p1.x - p2.x
        val deltaY = p1.y - p2.y
        val newPoint1 = p1.plusXY(deltaX, deltaY)
        val newPoint2 = p2.plusXY(-deltaX, -deltaY)
        return setOf(newPoint1, newPoint2).intersect(gridFields)
    }

    private fun lineExtension(p1: Point, p2: Point): Set<Point> {
        val deltaX = p2.x - p1.x
        val deltaY = p2.y - p1.y

        val extraPoints = mutableSetOf<Point>()
        var currentPoint = p1
        while (currentPoint in gridFields) {
            extraPoints += currentPoint
            currentPoint = currentPoint.plusXY(deltaX, deltaY)

        }
        currentPoint = p2
        while (currentPoint in gridFields) {
            extraPoints += currentPoint
            currentPoint = currentPoint.plusXY(-deltaX, -deltaY)
        }

        return extraPoints
    }

}


