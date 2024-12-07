package adventofcode

fun main() {
    Day03(test=false).showResult()
}

class Day03(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Mull It Over", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val regex = Regex("""mul\(\d{1,3},\d{1,3}\)""")
        val commandList = inputLines
            .flatMap{ line -> regex.findAll(line).toList().map { it.value } }

        return commandList
            .sumOf { it.multiply() }
    }

    override fun resultPartTwo(): Any {
        val regex = Regex("""do\(\)|don't\(\)|mul\(\d{1,3},\d{1,3}\)""")
        val commandList = inputLines(testFile = "example2")
            .flatMap{ line -> regex.findAll(line).toList().map { it.value } }

        var use = true
        var total = 0L
        commandList.forEach {command ->
            when (command.operator()) {
                "do" -> use = true
                "don't" -> use = false
                "mul" -> if (use) {
                    total += command.multiply()
                }
            }
        }
        return total
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


