package adventofcode

fun main() {
    Day03(test=false).showResult()
}

class Day03(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val regex = Regex("mul\\([0-9]{1,3},[0-9]{1,3}\\)")
        return inputLines
            .sumOf { it.calculateLine(regex) }
    }

    override fun resultPartTwo(): Any {
        val regex = Regex("do\\(\\)|don't\\(\\)|mul\\([0-9]{1,3},[0-9]{1,3}\\)")
        val commandList = inputLines(testFile = "example2")
            .map{ line -> regex.findAll(line).toList().map { it.value } }

        var use = true
        var total = 0L
        commandList.forEach {line ->
            line.forEach{ command ->
                when (command.operator()) {
                    "do" -> use = true
                    "don't" -> use = false
                    "mul" -> if (use) {
                        total += command.multiply()
                    }
                }
            }
        }
        return total
    }

    private fun String.calculateLine(regex: Regex): Long {
        return regex.findAll(this).sumOf { it.value.multiply() }
    }

    private fun String.operator(): String {
        return this.substringBefore("(")
    }

    private fun String.multiply(): Long {
        val operand1 = this.substringAfter("(").substringBefore(",")
        val operand2 = this.substringAfter(",").substringBefore(")")

        return operand1.toLong() * operand2.toLong()
    }
}


