package adventofcode

import tool.coordinate.twodimensional.Point

fun main() {
    Day12(test=false).showResult()
}

class Day12(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Garden Groups", hasInputFile = true) {

    private val gardenPlot = inputAsGrid()

    override fun resultPartOne(): Any {
        val areas = determineAreas()
        val number = areas.sumOf{ it.size * it.getPerimeter() }
        return number
    }

    override fun resultPartTwo(): Any {
        val areas = determineAreas()
        val number = areas.sumOf{ it.size * it.getSideCount() }
        return number
    }

    private fun determineAreas(): List<Set<Point>> {
        val result = mutableListOf<Set<Point>>()
        val notVisited = gardenPlot.keys.toMutableSet()
        while (notVisited.isNotEmpty()) {
            val newArea = notVisited.first().getArea()
            result.add(newArea)
            notVisited.removeAll(newArea)
        }
        return result
    }

    private fun Point.getArea(): Set<Point> {
        val letter = gardenPlot[this]!!
        var result = mutableSetOf(this)
        val queue = ArrayDeque<Point>()
        queue.add(this)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val newPoints = current
                .neighbors()
                .filter { nb -> gardenPlot.contains(nb) &&  gardenPlot[nb] == letter && nb !in result }

            result.addAll(newPoints)
            queue.addAll(newPoints)
        }
        return result
    }

    private fun Set<Point>.getPerimeter(): Int {
        return this.sumOf{ 4 - it.neighbors().intersect(this).size }
    }

    private fun Set<Point>.getSideCount(): Int {
        var upSet = this.filter{ p -> p.up() !in this}.groupBy { it.y }.mapValues { it.value.map{ it.x}.sorted() }.values.sumOf{it.countGroups()}
        var downSet = this.filter{ p -> p.down() !in this}.groupBy { it.y }.mapValues { it.value.map{ it.x}.sorted() }.values.sumOf{it.countGroups()}
        var leftSet = this.filter{ p -> p.left() !in this}.groupBy { it.x }.mapValues { it.value.map{ it.y}.sorted() }.values.sumOf{it.countGroups()}
        var rightSet = this.filter{ p -> p.right() !in this}.groupBy { it.x }.mapValues { it.value.map{ it.y}.sorted() }.values.sumOf{it.countGroups()}

        return upSet + downSet + leftSet + rightSet
    }

    private fun List<Int>.countGroups() : Int {
        var count = 1;
        for (i in 1 until this.size) {
            if (this[i] - this[i-1] > 1)
                count++
        }
        return count
    }


}


