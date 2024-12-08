package adventofcode

fun main() {
    Day07(test=false).showResult()
}

class Day07(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Bridge Repair", hasInputFile = true) {

    private val equationList = inputLines.map { line -> Equation.of(line) }

    override fun resultPartOne(): Any {
        return equationList
            .filter{ equation -> equation.canBeSolved(setOf(Operation.ADD, Operation.MULTIPLY)) }
            .sumOf { equation -> equation.outcome }
    }

    override fun resultPartTwo(): Any {
        return equationList
            .filter{ equation -> equation.canBeSolved(setOf(Operation.ADD, Operation.MULTIPLY, Operation.CONCATENATE)) }
            .sumOf { equation -> equation.outcome }
    }

    private fun Equation.canBeSolved(operationSet: Set<Operation>, subResult: Long=this.operands[0], operandsUsed: Int = 1): Boolean {

        return if (subResult > this.outcome) {
            false
        } else if (operandsUsed == this.operands.size) {
            subResult == this.outcome
        } else {
            operationSet.any { operation ->
                this.canBeSolved(
                    operationSet = operationSet,
                    subResult = calculate(operation, subResult, this.operands[operandsUsed]),
                    operandsUsed = operandsUsed + 1
                )
            }
        }
    }

    private fun calculate(operator: Operation, operand1: Long, operand2: Long): Long {
        return when (operator) {
            Operation.ADD -> operand1 + operand2
            Operation.MULTIPLY -> operand1 * operand2
            Operation.CONCATENATE -> {
                val concatenated = operand1.toString() + operand2.toString()
                if (concatenated.length >= 19) { //check on possibly going over Long.MAX_VALUE
                    throw Exception("Possibly TOO BIG!")
                }
                (operand1.toString() + operand2.toString()).toLong()
            }
        }
    }
}

data class Equation(val outcome: Long, val operands: List<Long>) {
    companion object {
        fun of(input: String): Equation {
            return Equation(
                outcome = input.substringBefore(":").toLong(),
                operands = input.substringAfter(": ").split(" ").map { it.toLong() }
            )
        }
    }
}

enum class Operation {
    ADD, MULTIPLY, CONCATENATE
}

