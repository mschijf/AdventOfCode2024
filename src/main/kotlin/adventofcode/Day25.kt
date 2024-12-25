package adventofcode

import tool.mylambdas.splitByCondition

fun main() {
    Day25(test=false).showResult()
}

class Day25(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Code Chronicle", hasInputFile = true) {

    private val schematics = inputLines.splitByCondition { it.isBlank() }
    private val lockList = schematics.filter { it.first() == "#####" }.map {it.heightListFromTop() }
    private val keyList = schematics.filter { it.last() == "#####" }.map {it.heightListFromBottom() }

    override fun resultPartOne(): Any {
        return lockList.sumOf {lock -> keyList.count { key -> lock.fitsWith(key)}}
    }

    override fun resultPartTwo(): Any {
        return "Last day: No part 2"
    }

    private fun List<Int>.fitsWith(key: List<Int>): Boolean {
        return this.withIndex().all { it.value + key[it.index] <= 5 }
    }

    private fun List<String>.heightListFromTop(): List<Int> {
        val result = MutableList(this.first().length) {0}
        for (col in this.first().indices) {
            for (row in 1..this.size-1) {
                if (this[row][col] == '#')
                    result[col]++
                else break
            }
        }
        return result
    }

    private fun List<String>.heightListFromBottom(): List<Int> {
        val result = MutableList(this.first().length) {0}
        for (col in this.first().indices) {
            for (row in this.size-2 downTo 0) {
                if (this[row][col] == '#')
                    result[col]++
                else break
            }
        }
        return result
    }
}



