package adventofcode

import tool.primarytype.digitLength

fun main() {
    Day11(test=false).showResult()
}

class Day11(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Plutonian Pebbles", hasInputFile = true) {

    private val stoneList = inputLines.first().split("\\s+".toRegex()).map { it.toLong() }

    override fun resultPartOne(): Any {
        return stoneList.evolve(25).size
    }

    override fun resultPartTwo(): Any {
        return stoneList.sumOf{it.evolveSmart(75)}
    }

    private fun List<Long>.evolve(blink: Int) : List<Long> {
        var result = this
        repeat(blink) {
            result = result.flatMap { it.evolve() }
        }
        return result
    }

    private fun Long.evolve(): List<Long> {
        when {
            this == 0L -> return listOf(1L)
            this.digitLength() % 2 == 1 -> return listOf(this * 2024)
            else -> {
                val strStone = this.toString()
                val half = strStone.length / 2
                return listOf(strStone.substring(0,half).toLong(), strStone.substring(half).toLong())
            }
        }
    }

    private val cache: MutableMap<Pair<Long, Int>, Long> = mutableMapOf()
    private fun Long.evolveSmart(blink: Int) : Long {
        if (blink == 0) {
            return 1
        }
        val key = Pair(this, blink)
        if (cache.containsKey(key)) {
            return cache[key]!!
        }
        val count = this.evolve().sumOf { stone ->
            stone.evolveSmart(blink-1)
        }
        cache[key] = count
        return count
    }

}


