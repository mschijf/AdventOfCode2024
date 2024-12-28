package adventofcode

import tool.mylambdas.splitByCondition
import kotlin.random.Random

fun main() {
    Day24(test = false).showResult()
}

class Day24(test: Boolean) : PuzzleSolverAbstract(test, puzzleName = "Crossed Wires", hasInputFile = true) {

    private val splittedInput = inputLines.splitByCondition { it.isBlank() }
    private val wireMap = splittedInput[0].associate { it.substringBefore(": ") to it.substringAfter(": ").toInt() }
    private val highestBitIndex = wireMap.filter { it.key.startsWith("x") }.maxOf { it.key.drop(1).toInt() }
    private val device = Device.of(splittedInput[1])

    override fun resultPartOne(): Any {
        println("correct:       51745744348272")
        return device.simulate(wireMap)?.toLong(2)?:throw Exception("Some error")
    }

    override fun resultPartTwo(): Any {
        println("correct:       bfq,bng,fjp,hkh,hmt,z18,z27,z31")
        return findWrongPairs()
            .flatMap { listOf(device.getOutputName(it.first), device.getOutputName(it.second)) }
            .sorted()
            .joinToString(",")
    }

    private fun findWrongPairs(
        currentBitIndex: Int = 0,
        swappedPairs: Set<Pair<Int, Int>> = emptySet(),
        allPairs: List<Pair<Int, Int>> = device.generateAllGateOutputPairs()
    ): Set<Pair<Int, Int>> {

        if (swappedPairs.size == 4) {
            return if (isCorrectAdder(highestBitIndex+1)) {
                swappedPairs
            } else {
                emptySet()
            }
        }

        if (isCorrectAdder(currentBitIndex)) {
            return (findWrongPairs( currentBitIndex + 1, swappedPairs, allPairs))
        }


        allPairs.forEach { toBeSwapped ->
            device.swapGateOutput(toBeSwapped.first, toBeSwapped.second)

            if (isCorrectAdder(currentBitIndex)) {
                val resultSet = findWrongPairs( currentBitIndex + 1, swappedPairs + toBeSwapped, allPairs-toBeSwapped)
                if (resultSet.isNotEmpty()) {
                    return resultSet
                }
            }
            device.swapGateOutput(toBeSwapped.first, toBeSwapped.second)
        }
        return emptySet()
    }

    private fun allSame(bitValue: Int): Map<String, Int> {
        val x = (0..highestBitIndex).associate { String.format("x%02d", it) to bitValue }
        val y = (0..highestBitIndex).associate { String.format("y%02d", it) to bitValue }
        return x + y
    }

    private fun randomOne(): Map<String, Int> {
        val x = (0..highestBitIndex).associate { String.format("x%02d", it) to Random.nextBits(1) }
        val y = (0..highestBitIndex).associate { String.format("y%02d", it) to Random.nextBits(1) }
        return x + y
    }

    private val checkList = List(1000) { randomOne() } + listOf(wireMap, allSame(1), allSame(0))
    private fun isCorrectAdder(tillSignificantBit: Int): Boolean {
        return checkList.all { isCorrectAdderForInput(it, tillSignificantBit) }
    }

    private fun isCorrectAdderForInput(bitMap: Map<String, Int>, tillSignificantBit: Int): Boolean {
        val xBits = bitMap.filterKeys { it.startsWith("x") }.toSortedMap().values.joinToString("").reversed()
        val yBits = bitMap.filterKeys { it.startsWith("y") }.toSortedMap().values.joinToString("").reversed()
        val zBits = (xBits.toLong(2) + yBits.toLong(2)).toString(2)
        val zBitsLeadingZeros = "0".repeat((highestBitIndex+2)-zBits.length) + zBits

        val zBitMapCalculated = device.simulate(bitMap)

        return zBitsLeadingZeros.takeLast(tillSignificantBit) == (zBitMapCalculated?.takeLast(tillSignificantBit) ?: return false)
    }
}


class Device(private val gateList: List<Gate>) {

    fun simulate(input: Map<String, Int>): String? {
        val wireValues = input.toMutableMap()
        val todo = gateList.toMutableSet()
        while (todo.isNotEmpty()) {
            val gate = todo.firstOrNull { it.op1 in wireValues && it.op2 in wireValues } ?: return null
            wireValues[gate.output] = gate.calculate(wireValues[gate.op1]!!, wireValues[gate.op2]!!)
            todo -= gate
        }
        return wireValues.filterKeys { it.startsWith("z") }.toSortedMap().values.joinToString("").reversed()
    }

    fun swapGateOutput(index1: Int, index2: Int) {
        val tmp = gateList[index1].output
        gateList[index1].output = gateList[index2].output
        gateList[index2].output = tmp
    }

    fun generateAllGateOutputPairs(): List<Pair<Int, Int>> {
        return (0..gateList.size-2).flatMap { first ->
            (first + 1..gateList.size-1)
                .map { second -> Pair(first, second)}
        }
    }

    fun getOutputName(index: Int) = gateList[index].output

    companion object {
        fun of (rawGateInput: List<String>): Device {
            return Device( rawGateInput.map { Gate.of(it) } )
        }
    }
}

data class Gate(val operator: String, val op1: String, val op2: String, var output: String) {

    override fun toString() = "$op1 $operator $op2 -> $output"

    fun calculate(op1: Int, op2: Int): Int {
        return when (this.operator) {
            "AND" -> op1 and op2
            "OR" -> op1 or op2
            "XOR" -> op1 xor op2
            else -> throw Exception("unknown operator")
        }
    }

    companion object {
        fun of(rawInput: String): Gate {
            //x00 AND y00 -> z00
            val parts = rawInput.split(" ")
            return Gate(parts[1], parts[0], parts[2], parts[4])
        }
    }
}
