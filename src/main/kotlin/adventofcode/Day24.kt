package adventofcode

import tool.mylambdas.splitByCondition
import kotlin.math.max
import kotlin.random.Random

fun main() {
    Day24(test = false).showResult()
}

class Day24(test: Boolean) : PuzzleSolverAbstract(test, puzzleName = "Crossed Wires", hasInputFile = true) {

    private val splittedInput = inputLines.splitByCondition { it.isBlank() }
    private val wireMap = splittedInput[0].map { it.substringBefore(": ") to it.substringAfter(": ").toInt() }.toMap()
    private val gateList = splittedInput[1].map { Gate.of(it) }

    private val initialX =
        wireMap.filterKeys { it.startsWith("x") }.toSortedMap().values.joinToString("").reversed().toLong(2)
    private val initialY =
        wireMap.filterKeys { it.startsWith("y") }.toSortedMap().values.joinToString("").reversed().toLong(2)

//    override fun resultPartOne(): Any {
//        println("correct:       51745744348272")
//        return calculateAll(wireMap)?.toLong(2)?:throw Exception("Some error")
//    }

    //checked: z** only right side of ->, x** and y** only on left side of ->
    //result: bfq,bng,fjp,hkh,hmt,z18,z27,z31

    override fun resultPartTwo(): Any {
        println()
        if (!solve(wireMap.size / 2, 0)) {
            println("NOT FOUND")
            controle(wireMap)
        }



        println("correct:       bfq,bng,fjp,hkh,hmt,z18,z27,z31")
        return "not yet"
    }

    private fun controle(bitMap: Map<String, Int>) {
//        val stringSet = "[(0, 61), (5, 210), (25, 114), (37, 47)]" --> allOnes
//        val stringSet = "[(0, 61), (12, 203), (13, 150), (112, 129)]"   //--> initialOne
        val stringSet = "[(72, 125), (31, 182), (77, 94), (56, 174)]"


        println("------------------------------------------")
        println("------------------------------------------")

        val swappers = stringSet
            .replace("(", "").replace(")", "").replace("[", "").replace("]", "")
            .split(",")
            .map { it.trim().toInt() }

        println(swappers)
        swappers.chunked(2).forEach { swp -> swapGateOutput(swp[0], swp[1]) }

        val resultX = bitMap.filterKeys { it.startsWith("x") }.toSortedMap().values.joinToString("").reversed()
        val resultY = bitMap.filterKeys { it.startsWith("y") }.toSortedMap().values.joinToString("").reversed()
        val resultZ = calculateAll(bitMap)

        val longX = resultX.toLong(radix = 2)
        val longY = resultY.toLong(radix = 2)
        val longZ = resultZ!!.toLong(radix = 2)
        val shouldBeZ = longX + longY

        println("found    : $longX + $longY = $longZ")
        println("should be: $longX + $longY = $shouldBeZ")

        val shouldBeZBits = shouldBeZ.toString(radix = 2)
        val diff = shouldBeZBits.mapIndexed { index, c -> if (resultZ[index] != c) '^' else ' ' }.joinToString("")

        println()
        println("found: $resultZ")
        println("must : $shouldBeZBits")
        println("       $diff")
        println("wrong z values: ${diff.count { it == '^' }}")
        println()

        println("------------------------------------------")
        println("------------------------------------------")

        println("46: ${isCorrectAdder(46)}")

    }

    private fun generateAllPairs(): List<Pair<Int, Int>> {

        println(gateList.indexOfFirst { it.resultName == "bng" })
        println(gateList.indexOfFirst { it.resultName == "fjp" })

        return (0..gateList.size-2).flatMap { first ->
            (first + 1..gateList.size-1)
                .map { second -> Pair(first, second)}
        }
    }

    private fun solve(
        highestBit: Int, currentBit: Int,
        swappedPairs: Set<Pair<Int, Int>> = emptySet(),
        allPairs: List<Pair<Int, Int>> = generateAllPairs()
    ): Boolean {

        if (currentBit > highestBit) {
            print("guess  :       ")
            println(swappedPairs.flatMap { listOf(gateList[it.first].resultName, gateList[it.second].resultName) }.sorted().joinToString(","))
            println(swappedPairs)
            return true
        }

        if (isCorrectAdder(currentBit)) {
            return (solve(highestBit, currentBit + 1, swappedPairs, allPairs))
        }

        if (swappedPairs.size > 3)
            return false

        print("$currentBit: swappedCandidates: ${swappedPairs.size} $swappedPairs ")
        println(swappedPairs.flatMap { listOf(gateList[it.first].resultName, gateList[it.second].resultName) }.sorted().joinToString(","))

        allPairs.forEach { toBeSwapped ->
            swapGateOutput(toBeSwapped.first, toBeSwapped.second)

            if (isCorrectAdder(currentBit)) {
                if (swappedPairs.size == 3) {
                    if (swappedPairs.toString() == "[(72, 125), (31, 182), (77, 94)]" && toBeSwapped.first == 56 && toBeSwapped.second == 174) {
                        println("JHAJSHAJSHJASH")
                    }
                }

                if (solve(highestBit, currentBit + 1, swappedPairs + toBeSwapped, allPairs-toBeSwapped)) {
                    return true
                }
            }
            swapGateOutput(toBeSwapped.first, toBeSwapped.second)
        }
        return false
    }

    private fun allOnes(): Map<String, Int> {
        val highestX = wireMap.filter { it.key.startsWith("x") }.maxOf { it.key.drop(1).toInt() }
        val highestY = wireMap.filter { it.key.startsWith("y") }.maxOf { it.key.drop(1).toInt() }

        return (0..highestX).associate { String.format("x%02d", it) to 1 } +
                (0..highestY).associate { String.format("y%02d", it) to 1 }
    }

    private fun randomOne(): Map<String, Int> {
        val highestX = wireMap.filter { it.key.startsWith("x") }.maxOf { it.key.drop(1).toInt() }
        val highestY = wireMap.filter { it.key.startsWith("y") }.maxOf { it.key.drop(1).toInt() }

        val x = (0..highestX).associate { String.format("x%02d", it) to Random.nextBits(1) }
        val y = (0..highestY).associate { String.format("y%02d", it) to Random.nextBits(1) }
        return x + y
    }

    val allOnes = allOnes()
    val checkList = List(1000) { randomOne() } // + listOf(wireMap, allOnes)
    private fun isCorrectAdder(tillSignificantBit: Int): Boolean {
        return checkList.all { isCorrectAdderForInput(it, tillSignificantBit) }
    }

    private fun isCorrectAdderForInput(bitMap: Map<String, Int>, tillSignificantBit: Int): Boolean {
        val xBits = bitMap.filterKeys { it.startsWith("x") }.toSortedMap().values.joinToString("").reversed()
        val yBits = bitMap.filterKeys { it.startsWith("y") }.toSortedMap().values.joinToString("").reversed()
        val zBits = (xBits.toLong(2) + yBits.toLong(2)).toString(2)
        val zBitsLeadingZeros = "0".repeat(46-zBits.length) + zBits

        val zBitMapCalculated = calculateAll(bitMap)

        val result = zBitsLeadingZeros.takeLast(tillSignificantBit) == (zBitMapCalculated?.takeLast(tillSignificantBit) ?: "false")
        return result
    }

    private fun swapGateOutput(index1: Int, index2: Int) {
        val tmp = gateList[index1].resultName
        gateList[index1].resultName = gateList[index2].resultName
        gateList[index2].resultName = tmp
    }

    private fun calculateAll(initialWireValues: Map<String, Int>): String? {
        val wireValues = initialWireValues.toMutableMap()
        val todo = gateList.toMutableSet()
        while (todo.isNotEmpty()) {
            val operation = todo.firstOrNull { it.op1 in wireValues && it.op2 in wireValues } ?: return null
            wireValues[operation.resultName] = operation.calculate(wireValues)
            todo -= operation
        }
        return wireValues.filterKeys { it.startsWith("z") }.toSortedMap().values.joinToString("").reversed()
    }

    private fun Gate.calculate(wireValues: MutableMap<String, Int>): Int {
        val op1 = wireValues[this.op1]!!
        val op2 = wireValues[this.op2]!!
        return when (this.gateType) {
            "AND" -> op1 and op2
            "OR" -> op1 or op2
            "XOR" -> op1 xor op2
            else -> throw Exception("unknown operator")
        }
    }


}

data class Gate(val gateType: String, val op1: String, val op2: String, var resultName: String) {

    override fun toString() = "$op1 $gateType $op2 -> $resultName"

    companion object {
        fun of(rawInput: String): Gate {
            return Gate(
                gateType = rawInput.substringAfter(" ").substringBefore(" "),
                op1 = rawInput.substringBefore(" "),
                op2 = rawInput.substringAfter(" ").substringAfter(" ").substringBefore(" ->"),
                resultName = rawInput.substringAfter(" -> ")
            )
        }
    }
}


//private fun findBackwardsTree(resultName: String, level: Int = 0):List<Operator> {
//    val operation = operationList.firstOrNull() { it.resultName == resultName }
//    if (operation != null) {
////            print("  ".repeat(level))
////            println(operation)
//        return findBackwardsTree(operation.op1, level+1) + findBackwardsTree(operation.op2, level+1) + operation
//    } else {
//        return emptyList()
//    }
//}


// calculateAll(bitMap.toMutableMap())
//
//val resultX = bitMap.filterKeys { it.startsWith("x") }.toSortedMap().values.joinToString("").reversed()
//val resultY = bitMap.filterKeys { it.startsWith("y") }.toSortedMap().values.joinToString("").reversed()
//val resultZ = bitMap.filterKeys { it.startsWith("z") }.toSortedMap().values.joinToString("").reversed()
//
//val longX = resultX.toLong(radix=2)
//val longY = resultY.toLong(radix=2)
//val longZ = resultZ.toLong(radix=2)
//val shouldBeZ = longX + longY
//
//println("found    : $longX + $longY = $longZ")
//println("should be: $longX + $longY = $shouldBeZ")
//
//val shouldBeZBits = shouldBeZ.toString(radix = 2)
//val diff = shouldBeZBits.mapIndexed { index, c -> if (resultZ[index] != c) '^' else ' ' }.joinToString("")
//
//println()
//println("found: $resultZ")
//println("must : $shouldBeZBits")
//println("       $diff")
//println("wrong z values: ${diff.count { it == '^' }}")
//println()
//
////        val firstDiff = diff.length - diff.lastIndexOf('^') - 1
////        val wrongResult = (if (firstDiff < 10) "z0" else "z") + firstDiff
////        println(wrongResult)
////        val allOps = findBackwardsTree(wrongResult)
//
//diff.forEachIndexed() { index, ch ->
//    if (ch == '^') {
//        val revIndex = diff.length - index - 1
//        val zzz = (if (revIndex < 10) "z0" else "z") + revIndex
//        val operation = operationList.firstOrNull() { it.resultName == zzz }
//        print("$zzz: ")
//        print (operation)
//        print("   ${resultZ[index]} -> ${shouldBeZBits[index]}")
//        print("  (${findBackwardsTree(operation!!.resultName).distinct().size})")
//        println()
//    }
//}
//
////        println(allOps.size)
////        println(allOps.distinct().size)
//
////        val allall = (0..diff.length-1).filter{diff[it] == '^'}.flatMap { findBackwardsTree( (if (it < 10) "z0" else "z") + it) }
////        println(allall.size)
////        println(allall.distinct().size)
//
//return resultZ.toLong(radix=2)

//val alreadyDone = mutableSetOf<String>()
//for (i1 in 0 .. operationList.size -2) {
//    for (i2 in i1+1 .. operationList.size - 1) {
//        swapOperationOutput(i1, i2)
//
//        for (j1 in 0 .. operationList.size -2) {
//            if (j1 == i1 || j1 == i2) continue
//            for (j2 in j1+1 .. operationList.size - 1) {
//                if (j2 == i1 || j2 == i2) continue
//                swapOperationOutput(j1, j2)
//
//                for (k1 in 0 .. operationList.size -2) {
//                    if (k1 == i1 || k1 == i2 || k1 == j1 || k1 == j2) continue
//                    for (k2 in k1+1 .. operationList.size - 1) {
//                        if (k2 == i1 || k2 == i2 || k2 == j1 || k2 == j2) continue
//                        swapOperationOutput(k1, k2)
//
//                        println("$i1, $i2, $j1, $j2, $k1, $k2")
//
//                        for (l1 in 0 .. operationList.size -2) {
//                            if (l1 == i1 || l1 == i2 || l1 == j1 || l1 == j2 || l1 == k1 || l1 == k2) continue
//                            for (l2 in l1+1 .. operationList.size - 1) {
//                                if (l2 == i1 || l2 == i2 || l2 == j1 || l2 == j2 || l2 == k1 || l2 == k2) continue
//                                swapOperationOutput(l1, l2)
//                                val outputList = makeOutputList(listOf(i1, i2, j1, j2, k1, k2, l1, l2))
//                                if (outputList in alreadyDone)
//                                    continue
//
//                                val resultZ = calculateAll(bitMap.toMutableMap())
//                                if (resultZ >= 0 && resultZ == resultX + resultY) {
//                                    return outputList
//                                }
//                                alreadyDone += outputList
//
//                                swapOperationOutput(l1, l2)
//                            }
//                        }
//                        swapOperationOutput(k1, k2)
//                    }
//                }
//                swapOperationOutput(j1, j2)
//            }
//        }
//        swapOperationOutput(i1, i2)
//    }
//}
//return "NOT FOUND"


/**
 * this solves the puzzle: see https://www.reddit.com/r/adventofcode/comments/1hl698z/2024_day_24_solutions/
 * some other code: https://github.com/eagely/adventofcode/blob/main/src/main/kotlin/solutions/y2024/Day24.kt
 *
 * more info: https://en.wikipedia.org/wiki/Adder_(electronics)#Ripple-carry_adder
 *
 * explanation: Our goal is to have a Ripple Carry Adder. To find the 8 wrong gates in a ripple carry adder circuit, we can break it down into three groups: The first 3 wrong gates can be found by looking at gates that produce a z output. In a correct ripple carry adder, any gate producing z must use an XOR operation. So we identify 3 gates that output z but don't use XOR and also are not gate z45, that one can be a bit different because it's the last gate - these are wrong. For the next 3 wrong gates, we need to understand that in a ripple carry adder, XOR operations should only be used when dealing with inputs and outputs, not for intermediate calculations. We can find 3 gates that use XOR but don't have x, y, or z in their inputs or outputs. Since these gates are using XOR in the wrong place, they are incorrect.
 *
 * Now we have 6 gates, but we don't know which ones to swap, to find the correct pairings, we want the number behind the first z-output in the recursive chain, so we write a recursive function. Say we have a chain of gates like this: a, b -> c where c is the output of one of our non-z XOR gates. Then c, d -> e then e, f -> z09 and we know we want to get to z09. Our recursive function would start with the first gate (a, b -> c), see that its output 'c' is used as input in the next gate, follow this to (c, d -> e), see that its output 'e' is used as input in the z09 gate, and finally reach (e, f -> z09). Now we just swap c and z09 - 1, so we swap c and z08. The -1 is there because this function finds the NEXT z gate, not the current one we need.
 *
 * The final 2 wrong gates require comparing the circuit's actual output with what we expect (x+y). When we convert both numbers to binary and compare them, we'll find a section where they differ. If we XOR these two binary numbers, we'll get zeros where the bits match and ones where they differ. Count the number of zeros at the start of this XOR result - let's call this number N. The last two wrong gates will be the ones that use inputs xN and yN, swap them.
 *
 */

//private fun solveCopy() {
//    val highestZ = "z" + operationList.filter{ it.resultName.startsWith("z") }.maxOf { it.resultName.drop(1).toInt() }
//    val wrong = mutableSetOf<String>()
//    operationList.forEach { operation ->
//        with (operation) {
//            //we're building an adder. so the resultbit 'z' should be the result of an XOR port
//            if (false) {
//                println("nono")
//            } else if (resultName[0] == 'z' && resultName != highestZ && operatorType != "XOR") {
//                wrong += resultName
//
//            } else if (operatorType == "XOR" && resultName[0] != 'z' && op1[0] !in "xy" && op2[0] !in "xy") {
//                wrong += resultName
//
//            } else if (operatorType == "AND" && op1 != "x00" && op2 != "x00") {
//                operationList.filter{ resultName == it.op1 || resultName == it.op2 }.forEach() { subOperation ->
//                    if ( subOperation.operatorType != "OR") {
//                        wrong += resultName
//                    }
//                }
//
//            } else if (operatorType == "XOR") {
//                operationList.filter{ resultName == it.op1 || resultName == it.op2 }.forEach() { subOperation ->
//                    if ( subOperation.operatorType == "OR") {
//                        wrong += resultName
//                    }
//                }
//
//            }
//        }
//    }
//    println(wrong.toList().sorted().joinToString(","))
//
//}
