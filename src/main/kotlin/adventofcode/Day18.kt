package adventofcode

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos

fun main() {
    Day18(test=false).showResult()
}

class Day18(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="RAM Run", hasInputFile = true) {

    private val maxX = if (test) 6 else 70
    private val maxY = if (test) 6 else 70
    private val nBytesFallen = if (test) 12 else 1024

    private val fallingBytes = inputLines.map{ pos(it) }

    override fun resultPartOne(): Any {
        return fallingBytes.take(nBytesFallen).toSet().shortestRoute()
    }

    override fun resultPartTwo(): Any {
        return lineairSearch()
    }

    private fun lineairSearch(): Point {
        var i = nBytesFallen+1
        while (fallingBytes.take(i).toSet().shortestRoute() > 0) {
            i++
        }
        return fallingBytes[i-1]
    }


//    private fun binarySearch():Point {
//        var l = 2957//nBytesFallen+1
//        var h = fallingBytes.size-1
//        while (l < h) {
//            var i = (l+h) / 2
//            if (fallingBytes.take(i).toSet().shortestRoute() > 0) {
//                l = i+1
//            } else {
//                h = i - 1
//            }
//        }
//        println("$l $h")
//        return fallingBytes[l]
//    }

    private fun Set<Point>.shortestRoute(start: Point=pos(0,0), end: Point=pos(maxX, maxY)): Int {
        val visited = mutableSetOf<Point>()
        val queue = ArrayDeque<Pair<Point, Int>>()
        queue.add(Pair(start, 0))
        visited += start
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current.first == end) {
                return current.second
            }
            current.first.neighbors().filter { nb -> nb.inMemorySpace() && nb !in visited && nb !in this}. forEach { step ->
                queue.add(Pair(step, current.second+1))
                visited += step
            }
//
//            queue.addAll(
//                current.first
//                    .neighbors()
//                    .filter { nb -> nb.inMemorySpace() && nb !in visited && nb !in bytesFallen}
//                    .map { Pair(it, current.second + 1) } )
        }
        return -1
    }

    fun Point.inMemorySpace(): Boolean {
        return this.x in 0..maxX && this.y in 0..maxY
    }


}


