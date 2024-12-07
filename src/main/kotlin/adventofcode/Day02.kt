package adventofcode

fun main() {
    Day02(test=false).showResult()
}

//642 too low

class Day02(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Red-Nosed Reports", hasInputFile = true) {

    private val reportsList = inputLines
        .map { line -> line.split("\\s+".toRegex()).map { it.toInt() } }

    override fun resultPartOne(): Any {
        return reportsList
            .count { it.isOkay() }
    }

    override fun resultPartTwo(): Any {
        return reportsList
            .count { it.isOkayWithDemping() }
    }

    private fun List<Int>.isOkay(): Boolean {
        if (this.size == 1)
            return true

        return if (this[0] < this[1]) {
            this.filterIndexed{index, value -> index > 0 && value - this[index-1] !in 1..3 }
                .isEmpty()
        } else {
            this.filterIndexed{index, value -> index > 0 && this[index-1] - value !in 1..3 }
                .isEmpty()
        }
    }

    private fun List<Int>.isOkayWithDemping(): Boolean {
        if (this.isOkay())
            return true

        if (this.drop(1).isOkay())
            return true

        for (i in this.indices) {
            if ( (this.subList(0, i) + this.subList(i + 1, this.size)).isOkay())
                return true
        }
        return false
    }
}


