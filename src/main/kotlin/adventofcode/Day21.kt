package adventofcode

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos

fun main() {
    Day21(test=false).showResult()
}

class Day21(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Keypad Conundrum", hasInputFile = true) {

    private val codeList = inputLines

    private val robotNumerical0 = KeyPath(numerical = true)
    private val robotDirectional1 = KeyPath(numerical = false)
    private val robotDirectional2 = KeyPath(numerical = false)

    override fun resultPartOne(): Any {
        return codeList.sumOf { code -> getManualInput(code).minOf { it.length } * code.dropLast(1).toInt() }
//        return codeList.map { code -> getManualInput(code).size }
    }

    override fun resultPartTwo(): Any {
        return "TODO"
    }

    private fun getManualInput(code: String) : List<String> {
        return robotNumerical0.allSequences(code).flatMap { r0Sequence ->
            robotDirectional1.allSequences(r0Sequence).flatMap { r1Sequence ->
                robotDirectional2.allSequences(r1Sequence)
            }
        }
    }
}


class KeyPath(numerical: Boolean) {
    private val keyPad: Map<Char, Point> =
        if (numerical)
            mapOf(
                '7' to pos(0, 0),
                '8' to pos(1, 0),
                '9' to pos(2, 0),
                '4' to pos(0, 1),
                '5' to pos(1, 1),
                '6' to pos(2, 1),
                '1' to pos(0, 2),
                '2' to pos(1, 2),
                '3' to pos(2, 2),
                '0' to pos(1, 3),
                'A' to pos(2, 3),
            )
        else
            mapOf(
                '^' to pos(1, 0),
                'A' to pos(2, 0),
                '<' to pos(0, 1),
                'v' to pos(1, 1),
                '>' to pos(2, 1),
            )

    private val gap = if (numerical) pos(0,3) else pos (0,0)

    fun allSequences(charPath: String, startChar: Char = 'A'): List<String> {
        val tmp = mutableListOf<List<String>>()
        val fullPath = startChar + charPath
        for (i in 1..< fullPath.length) {
            tmp.add(allPaths(fullPath[i-1], fullPath[i]))
        }
        return tmp.allCombinations()
    }

    private fun allPaths (fromButton: Char, toButton: Char): List<String> {
        val result = mutableListOf<String>()
        val endPoint = keyPad[toButton]!!
        val fromPoint = keyPad[fromButton]!!
        val maxLength = fromPoint.distanceTo(endPoint)

        val queue = ArrayDeque<Pair<Point, String>>()
        queue.add(Pair(keyPad[fromButton]!!, ""))
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current.first == endPoint) {
                result.add(current.second + "A")
            } else if (current.second.length < maxLength){
                current.first.neighbors().filter { nb -> nb != gap && nb.distanceTo(endPoint) < current.first.distanceTo(endPoint) }.forEach { nb ->
                    queue.add(Pair(nb, current.second + current.first.getDir(nb).directionSymbol))
                }
            }
        }
        return result
    }

    private var cache = mutableMapOf<List<List<String>>, List<String>>()
    private fun List<List<String>>.allCombinations() : List<String> {
        if (this.size == 1) {
            return this.first()
        }
        if (cache.contains(this)) {
            return cache[this]!!
        }
        val result = mutableListOf<String>()
        this[0].forEach { str ->
            val subResult = this.drop(1).allCombinations()
            subResult.forEach {
                result.add(str + it)
            }
        }
        cache[this] = result
        return result
    }

}


fun Point.getDir(to: Point): Direction {
    return if (this.x == to.x) {
        if (this.y == to.y)
            throw Exception ("from and to is same position")
        else
           if (this.y < to.y) Direction.DOWN else Direction.UP
    } else if (this.y == to.y) {
        if (this.x < to.x) Direction.RIGHT else Direction.LEFT
    } else {
        throw Exception ("no dir can be found")
    }
}



