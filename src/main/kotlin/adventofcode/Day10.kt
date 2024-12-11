package adventofcode

import tool.coordinate.twodimensional.Point

fun main() {
    Day10(test=false).showResult()
}

class Day10(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Hoof It", hasInputFile = true) {

    private val topoMap = inputAsGrid().mapValues { it.value.digitToInt() }


    override fun resultPartOne(): Any {
        return topoMap
            .filterValues { it == 0 }
            .keys
            .sumOf { countFrom(it) }
    }

    override fun resultPartTwo(): Any {
        return topoMap
            .filterValues { it == 0 }
            .keys
            .sumOf { count2From(it) }
    }

    private fun countFrom(fromPoint: Point): Int {
        val visited = mutableSetOf<Point>()
        var count = 0
        val queue = ArrayDeque<Point>()
        queue.add(fromPoint)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (topoMap[current]!! == 9 && current !in visited) {
                count++
                visited.add(current)
            }
            val level = topoMap[current]!!
            queue.addAll(
                current
                    .neighbors()
                    .filter{ nb -> topoMap.getOrDefault(nb, -1)  == level+1 })
        }
        return count
    }

    private fun count2From(fromPoint: Point): Int {
        var count = 0
        val queue = ArrayDeque<Point>()
        queue.add(fromPoint)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (topoMap[current]!! == 9) {
                count++
            }
            val level = topoMap[current]!!
            queue.addAll(
                current
                    .neighbors()
                    .filter{ nb -> topoMap.getOrDefault(nb, -1)  == level+1 })
        }
        return count
    }
}
