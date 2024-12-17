package adventofcode

fun main() {
    Day17(test=false).showResult()
}

class Day17(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Chronospatial Computer", hasInputFile = true) {


    override fun resultPartOne(): Any {
        val program = Program(inputLines)
        program.execute()
        return program.getOutput()
    }

    override fun resultPartTwo(): Any {
        if(test) {
            return "Part 2 only works for real input"
        }
        val programString = Program(inputLines).getInstructionListAsString()
        val initARegisterSet = programString.findStartValueForRegisterA()
        return initARegisterSet.min()
    }

    private fun String.findStartValueForRegisterA(currentInitA: Long = 0L): Set<Long> {
        if (this == outputOfProgramForInitialValue(currentInitA)) {
            return setOf(currentInitA)
        }
        val solutionSet = mutableSetOf<Long>()
        for (i in 0..7) {
            val out = outputOfProgramForInitialValue(8*currentInitA + i)
            if (this.endsWith(out)) {
                solutionSet += this.findStartValueForRegisterA(8*currentInitA + i)
            }
        }
        return solutionSet
    }

    private fun outputOfProgramForInitialValue(initValue: Long): String {
        val program = Program(inputLines)
        program.overrideRegisterA(initValue)
        program.execute()
        return program.getOutput()
    }

}



class Program(init: List<String>) {
    private var registerA = init[0].substringAfter("Register A: ").toLong()
    private var registerB = init[1].substringAfter("Register B: ").toLong()
    private var registerC = init[2].substringAfter("Register C: ").toLong()
    private val instructionListString = init[4].substringAfter("Program: ")
    private val instructionList = init[4].substringAfter("Program: ").split(",").map { it.toInt() }

    private var output: String = ""
    private var instructionPointer = 0

    fun overrideRegisterA(value: Long) {
        registerA = value
    }

    fun getOutput(): String {
        return output
    }

    fun getInstructionListAsString(): String {
        return instructionListString
    }

    fun execute() {
        while (instructionPointer < instructionList.size) {
            executeInstruction()
        }
    }

    private fun executeInstruction() {
        val opCode = instructionList[instructionPointer]
        val operand = instructionList[instructionPointer+1]
        when(opCode) {
            0 -> { //adv
                registerA = registerA / (1 shl getComboValue(operand).toInt() )
                instructionPointer+=2
            }
            1 -> { // bxl
                registerB = registerB xor operand.toLong()
                instructionPointer+=2
            }
            2 -> { //bst
                registerB = getComboValue(operand) % 8
                instructionPointer+=2
            }
            3 -> { //jnz
                if (registerA != 0L) {
                    instructionPointer = operand / 2
                } else {
                    instructionPointer+=2
                }
            }
            4 -> { // bxc
                registerB = registerB xor registerC
                instructionPointer+=2
            }
            5 -> { //out
                output = (if (output.isEmpty()) "" else "$output,") + "${(getComboValue(operand) % 8)}"
                instructionPointer+=2
            }
            6 -> { //bdv
                registerB = registerA / (1 shl getComboValue(operand).toInt() )
                instructionPointer+=2
            }
            7 -> { //cdv
                registerC = registerA / (1 shl getComboValue(operand).toInt() )
                instructionPointer+=2
            }
        }
    }

    private fun getComboValue(operand: Int): Long {
        return when (operand) {
            0, 1, 2, 3 -> operand.toLong()
            4 -> registerA
            5 -> registerB
            6 -> registerC
            else -> throw Exception("Not expected operand")
        }
    }
}


