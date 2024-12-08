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
        val allAntinodeLocations = antennaGroupMap.values.flatMap { it.determineAntinodeLocationsOneStep() }.toSet()
        return allAntinodeLocations.size
    }

    override fun resultPartTwo(): Any {
        val allAntinodeLocations = antennaGroupMap.values.flatMap { it.determineAntinodeLocationsInLine() }.toSet()
        return allAntinodeLocations.size
    }

    private fun List<Point>.determineAntinodeLocationsOneStep(): Set<Point> {
        return this
            .toCombinedItemsList()
            .flatMap{ oneStepExtension(it.first, it.second) }
            .toSet()
    }

    private fun List<Point>.determineAntinodeLocationsInLine(): Set<Point> {
        return this
            .toCombinedItemsList()
            .flatMap{ lineExtension(it.first, it.second) }
            .toSet()
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

        val oneDirection = generateSequence<Point>(p1) { aPoint -> aPoint.plusXY(deltaX, deltaY) }
            .takeWhile { it in gridFields }
            .toSet()
        val otherDirection = generateSequence<Point>(p2) { aPoint -> aPoint.plusXY(-deltaX, -deltaY) }
            .takeWhile { it in gridFields }
            .toSet()
        return oneDirection + otherDirection
    }

}


