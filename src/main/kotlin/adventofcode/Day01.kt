package adventofcode

import kotlin.math.absoluteValue

fun main() {
    Day01(test=false).showResult()
}

class Day01(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Historian Hysteria", hasInputFile = true) {

    private val lines = inputLines
        .map { line -> line.split("\\s+".toRegex()).map { it.toInt() } }

    private val list1 = lines.map { it[0] }.sorted()
    private val list2 = lines.map { it[1] }.sorted()

    override fun resultPartOne(): Any {

        val total = list1
            .mapIndexed{ index, value -> (value - list2[index]).absoluteValue }
            .sum()

        return total
    }

    override fun resultPartTwo(): Any {
        val total = list1
            .sumOf { item1 -> item1 * list2.count{ item2 -> item1 == item2} }

        return total
    }
}


