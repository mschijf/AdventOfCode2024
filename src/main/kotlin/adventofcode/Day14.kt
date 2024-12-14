package adventofcode

import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.coordinate.twodimensional.printAsGrid
import kotlin.math.max

fun main() {
    Day14(test=false).showResult()
}

class Day14(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Restroom Redoubt", hasInputFile = true) {

    private val maxX = if (test) 11 else 101
    private val maxY = if (test) 7 else 103

    override fun resultPartOne(): Any {
        val robotList = inputLines.map{Robot.of(it)}
        repeat(100) {
            robotList.forEach { it.move(maxX, maxY) }
        }

        return robotList
            .map { it.pos.quadrant() }
            .filter{ it >= 0 }
            .groupingBy { it }.eachCount()
            .values
            .reduce { acc, i -> acc * i }
    }

    override fun resultPartTwo(): Any {
        if (test) {
            return "Works only in real mode"
        }
        val robotList = inputLines.map{Robot.of(it)}
        val count = robotList.loopUntilTree()
//        robotList.printRobotConstellation()
        return count
    }

    private fun Point.quadrant(): Int {
        return if (x in 0..< maxX/2 && y in 0..< maxY/2)
            0
        else if (x in maxX/2+1..< maxX && y in 0..< maxY/2)
            1
        else if (x in 0..< maxX/2 && y in maxY/2+1 ..< maxY)
            2
        else if (x in maxX/2+1..< maxX && y in maxY/2+1 ..< maxY)
            3
        else
            -1
    }

    private fun List<Robot>.printRobotConstellation() {
        this
            .map{it.pos}
            .groupingBy { it }.eachCount()
            .printAsGrid { it.toString() }
    }

    private fun List<Robot>.loopUntilTree(): Int {
        for (count in 1 .. 1_000_000_000) {
            this.forEach { it.move(maxX, maxY) }
            if (this.hasPartOfTree())
                return count
        }
        return -1
    }

    /**
     * check if there are a number of robots straight to each other in one line.
     * First I tried 5, but that didn't give me a tree picture. Then I tried 7: with result!
     */
    private fun List<Robot>.hasPartOfTree(): Boolean {
        val robotsNextToEachOther = 7
        val xx = this.map{it.pos}.toSet()
        val map = xx.groupBy { it.y }.filter { it.value.size > robotsNextToEachOther }
        map.keys.forEach { l ->
            if (map[l]!!.sortedBy{it.x}.maxLengthGroup() >= robotsNextToEachOther)
                return true
        }
        return false
    }

    private fun List<Point>.maxLengthGroup() : Int {
        var max = 0
        var length = 0
        for (i in 1..< this.size) {
            if (this[i].x - this[i-1].x > 1) {
                max = max (length, max)
                length = 0
            } else {
                length++
            }
        }
        return max
    }


}

data class Robot(var pos: Point, val speed: Point) {
    companion object {
        fun of(line: String): Robot {
            return Robot(
                pos = pos(line.substringAfter("p=").substringBefore(" ")),
                speed=pos(line.substringAfter("v="))
            )
        }
    }

    fun move(maxX: Int, maxY: Int) {
        pos = pos( (pos.x+speed.x).mod(maxX), (pos.y+speed.y).mod(maxY) )
    }
}


