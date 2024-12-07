package adventofcode

import tool.mylambdas.collectioncombination.allOfCombinedItems
import tool.mylambdas.collectioncombination.toCombinedItemsList
import tool.mylambdas.splitByCondition

fun main() {
    Day05(test=false).showResult()
}

class Day05(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Print Queue", hasInputFile = true) {

    private val input = inputLines.splitByCondition { it.isBlank() }
    private val rules = input.first()
        .map { it.substringBefore('|').toInt() to it.substringAfter('|').toInt() }
        .groupBy({ it.first }, { it.second })

    private val pages = input.last()
        .map{line -> line.split(",").map { it.toInt() }}

    override fun resultPartOne(): Any {
        return pages.filter { it.isOkay() }.sumOf {it[it.size/2]}
    }

    override fun resultPartTwo(): Any {
        return pages.filterNot { it.isOkay() }.sumOf { it.findMiddle() }
    }

    private fun List<Int>.isOkay(): Boolean {
        return this.allOfCombinedItems { i1, i2 -> i1 !in rules.getOrDefault(i2, emptyList()) }
    }

    private fun List<Int>.findMiddle(): Int {
        val countMap = this.associate { it to 0 }.toMutableMap()
        this.toCombinedItemsList().forEach { (i1, i2) ->
            if (i2 in rules.getOrDefault(i1, emptyList())) {  //i1 hoort voor i2
                countMap[i1] = countMap[i1]!! + 1
            } else if (i1 in rules.getOrDefault(i2, emptyList())) {  // i2 hoort voor i1
                countMap[i2] = countMap[i2]!! + 1
            } else {
                //geen punten voor i1 of i2
            }
        }
        // sorteer the map by 'how many time a number should before another one' and take after sorting the middle one.
        return countMap.entries.sortedBy { it.value }[countMap.size/2].key
    }

}


