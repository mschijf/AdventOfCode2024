package adventofcode

import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos


fun main() {
    Day21(test=false).showResult()
}

/**
 * See below for description of thoughts
 */

class Day21(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Keypad Conundrum", hasInputFile = true) {

    private val codeList = inputLines

    private val numericalKeyPad = KeyPad(numerical = true)
    private val directionalKeyPad = KeyPad(numerical = false)

    override fun resultPartOne(): Any {
        println("correct:       213536")
        return determineLength(2)
    }

    override fun resultPartTwo(): Any {
        println("correct:       258369757013802")
        return determineLength(25)
    }

    private fun determineLength(directionalRobotChainLength: Int): Long {
        val robotChain = listOf(RobotArm(numericalKeyPad)) + List(directionalRobotChainLength){ RobotArm(directionalKeyPad) }
        robotChain.zipWithNext { a, b ->  a.setControllingRobot(b)}
        return codeList.sumOf { code -> robotChain[0].shortestSequenceLength(code) * code.dropLast(1).toInt() }
    }
}

class RobotArm(
    private val keyPad: KeyPad) {

    private var controllingRobot: RobotArm? = null

    fun setControllingRobot(robot: RobotArm) {
        controllingRobot = robot
    }

    private val fromToMap = mutableMapOf<Pair<Char, Char>, Long>()
    private fun shortestSequenceFromCharToChar(from: Char, to: Char): Long {
        val key = Pair(from, to)
        if (fromToMap.contains(key))
            return fromToMap[key]!!

        val paths = keyPad.getBestPaths(from, to)
        val pathLength = paths.minOf { path ->
            ("A"+path).windowed(2,1).sumOf { it ->  controllingRobot?.shortestSequenceFromCharToChar(it[0], it[1]) ?: 1}
        }

        fromToMap[key] = pathLength
        return pathLength
    }

    fun shortestSequenceLength(code: String): Long {
        return ("A" + code).windowed(2,1).sumOf { it ->  this.shortestSequenceFromCharToChar(it[0], it[1]) }
    }
}

class KeyPad(numerical: Boolean) {
    private val keyPad: Map<Char, Point> =
        if (numerical)
            mapOf(
                '7' to pos(0, 0), '8' to pos(1, 0), '9' to pos(2, 0),
                '4' to pos(0, 1), '5' to pos(1, 1), '6' to pos(2, 1),
                '1' to pos(0, 2), '2' to pos(1, 2), '3' to pos(2, 2),
                '0' to pos(1, 3), 'A' to pos(2, 3),
            )
        else
            mapOf(
                '^' to pos(1, 0), 'A' to pos(2, 0),
                '<' to pos(0, 1), 'v' to pos(1, 1), '>' to pos(2, 1),
            )
    private val gap = if (numerical) pos(0,3) else pos (0,0)

    private val bestPaths = keyPad.keys.flatMap { button1 ->
        keyPad.keys.map { button2 ->
            Pair(button1, button2) to allPaths(button1, button2)
        }
    }.toMap()

    fun getBestPaths(from: Char, to: Char) = bestPaths[Pair(from, to)]!!

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

    //todo: move to Point class
    private fun Point.getDir(to: Point): Direction {
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
}


/**
 * After having a program that created all lists of lists of chars and determine the shortest - which worked for part 1,
 * I needed something else for part2. So I started more or less from scratch again
 *
 * First, I created a seperate class KeyPad, in which I pre-calculated all optimal paths to come from one button to another.
 * Obviously, there are two types of these classes: Directional and Numerical. The only difference is the set of buttons and their configuration
 *
 * Then I created a RobotArm class. The robotArm has a keyPad it uses and knows its "controller".
 * With that controller, you can create a chain of RobotArms, as follows
 *     (num is robot with numerical keypad and dir is robot with directional keypad):
 *
 * num <- dir1 <- dir2 <- ...... <- dir25 <- "human"
 *
 * Now, if you need to determine the shortest sequence for 0295A on human level,
 * you need to determine the shortest sequence on human level for each
 *     "A->0", "0->2", "2->9", "9->5" and finally "5->A"  and then sum those.
 *
 *     For instance, to go from 2->9, you have three sequences >^^A, ^>^A and ^^>A
 *     For each of these sequences, you determine how to get the shortest sequence on human level, for instance for the first sequence >^^^A:
 *         to do this, you need to determine the shortest sequence on human level for each
 *         "A to >", "> to ^", "^ to ^", "^ to ^", "^ to A"   and then sum those.
 *
 *          For instance, to go from > to ^, you have two sequences "^<" and "<^"
 *          For each of these sequences, you determine how to get the shortest sequence on human level, for instance for the first sequence >^^^A:
 *            .... etc. ....
 *
 *          on the deepest level (where there is no 'controlling robot arm), the press on a button is just 1.
 *
 * by maintaining caches for each robotarm, you can quickly determine the shortest sequence.
 *
 */
