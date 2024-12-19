package adventofcode

import tool.mylambdas.splitByCondition

fun main() {
    Day19(test=false).showResult()
}

class Day19(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Linen Layout", hasInputFile = true) {

    private val towelList = inputLines.splitByCondition { it.isBlank() }.first().first().split(", ")
    private val patternList = inputLines.splitByCondition { it.isBlank() }.last()

    override fun resultPartOne(): Any {
        return patternList.count { pattern -> canPatternBeMade(pattern) }
    }

    override fun resultPartTwo(): Any {
        return patternList.sumOf { pattern -> allPossibleArrangements(pattern) }
    }

    private fun canPatternBeMade(pattern: String): Boolean {
        return if (pattern.isEmpty())
            true
        else
            towelList
                .filter { towel ->  pattern.startsWith(towel)}
                .any{ towel -> canPatternBeMade(pattern.substring(towel.length)) }
    }

    private fun allPossibleArrangements(pattern: String, alreadyCheckedBefore: MutableMap<String, Long> = mutableMapOf()): Long {
        return if (pattern.isEmpty()) {
            1
        } else if (pattern in alreadyCheckedBefore) {
            alreadyCheckedBefore[pattern]!!
        } else {
            val subTotal = towelList
                .filter { towel -> pattern.startsWith(towel) }
                .sumOf { towel -> allPossibleArrangements(pattern.substring(towel.length), alreadyCheckedBefore) }
            alreadyCheckedBefore[pattern] = subTotal
            subTotal
        }
    }

}


