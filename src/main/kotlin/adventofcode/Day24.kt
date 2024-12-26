package adventofcode

import tool.mylambdas.splitByCondition

fun main() {
    Day24(test=false).showResult()
}

class Day24(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Crossed Wires", hasInputFile = true) {

    private val splittedInput = inputLines.splitByCondition { it.isBlank() }
    private val bitMap = splittedInput[0].map { it.substringBefore(": ") to it.substringAfter(": ").toInt()}.toMap()
    private val operationList = splittedInput[1].map {Operator.of(it)}

    override fun resultPartOne(): Any {
        return calculateAll(bitMap.toMutableMap())
    }

    //checked: z** only right side of ->, x** and y** only on left side of ->
    override fun resultPartTwo(): Any {

//        return listOf("ftt", "z39", "z28", "wmd", "vgg", "gsg", "jss", "nww").sorted().joinToString(",")

//        swapOperationOutput(112, 129)
//        swapOperationOutput(13, 150)
//        swapOperationOutput(12, 203)
//        swapOperationOutput(0, 61)

        //laatste: gevonden!! [0, 61, 30, 182, 56, 221, 203, 217]


//        val resultX = bitMap.filterKeys { it.startsWith("x") }.toSortedMap().values.joinToString("").reversed().toLong(radix=2)
//        val resultY = bitMap.filterKeys { it.startsWith("y") }.toSortedMap().values.joinToString("").reversed().toLong(radix=2)
//        val shouldBeZBits = (resultX + resultY).toString(radix = 2)
//
//        val resultZOrg = calculateAll(bitMap)
//        println ("should be    : $resultX + $resultY = ${resultX + resultY}")
//        println ("but we got   : $resultX + $resultY = $resultZOrg")
//
//        val resultZOrgBits = resultZOrg.toString(radix = 2)
//        val diffOrg = shouldBeZBits.mapIndexed { index, c -> if (resultZOrgBits[index] != c) '^' else ' ' }.joinToString("")
//
//        println()
//        println("found: $resultZOrgBits")
//        println("must : $shouldBeZBits")
//        println("       $diffOrg")
//        println("wrong z values: ${diffOrg.count { it == '^' }}")
//        println("=====================================================================================")
//        println()
//
//        diffOrg.solve(shouldBeZBits)
//
//
        solveCopy()
        return "not yet"
    }

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
    private fun solveCopy() {
        val highestZ = "z" + operationList.filter{ it.resultName.startsWith("z") }.maxOf { it.resultName.drop(1).toInt() }
        val wrong = mutableSetOf<String>()
        operationList.forEach { operation ->
            with (operation) {
                //we're building an adder. so the resultbit 'z' should be the result of an XOR port
                if (resultName[0] == 'z' && resultName != highestZ && operatorType != "XOR") {
                    wrong += resultName

                } else if (operatorType == "XOR" && resultName[0] != 'z' && op1[0] !in "xy" && op2[0] !in "xy") {
                    wrong += resultName

                } else if (operatorType == "AND" && op1 != "x00" && op2 != "x00") {
                    operationList.filter{ resultName == it.op1 || resultName == it.op2 }.forEach() { subOperation ->
                        if ( subOperation.operatorType != "OR") {
                            wrong += resultName
                        }
                    }

                } else if (operatorType == "XOR") {
                    operationList.filter{ resultName == it.op1 || resultName == it.op2 }.forEach() { subOperation ->
                        if ( subOperation.operatorType == "OR") {
                            wrong += resultName
                        }
                    }

                }
            }
        }
        println(wrong.toList().sorted().joinToString(","))

        //result: bfq,bng,fjp,hkh,hmt,z18,z27,z31
    }

    private fun String.solve(shouldBeZBits: String, alreadyChoosen:List<Int> = emptyList(), level: Int=4): Boolean {
        if (level == 0) {
            if (this.none{it == '^'}) {
                println("gevonden!! $alreadyChoosen")
                if (verifySecond()) {
                    println("ECHT gevonden!! $alreadyChoosen")
                    return true
                }
                return false
            }
            return false
        }
        val lastOrgMisMatch = this.lastIndexOf('^')
        for (i in 0 .. operationList.size -2) {
            if (i in alreadyChoosen) continue
            for (j in i + 1..operationList.size - 1) {
                if (j in alreadyChoosen) continue

                swapOperationOutput(i, j)
                val resultZ = calculateAll(bitMap)
                val resultZBits = resultZ.toString(radix = 2)
                if (resultZBits.length == shouldBeZBits.length) {
                    val diffBits = shouldBeZBits.mapIndexed { index, c -> if (resultZBits[index] != c) '^' else ' ' }.joinToString("")

                    val lastMisMatch = diffBits.lastIndexOf('^')
                    if (lastMisMatch < lastOrgMisMatch) {
                        if (diffBits.solve(shouldBeZBits,alreadyChoosen+listOf(i, j), level-1) == true) {
                            println("$i:${operationList[i].resultName}  $j:${operationList[j].resultName}")
                            return true
                        }
                    }
                }
                swapOperationOutput(i, j)
            }
        }
        return false
    }

    private fun verifySecond():Boolean {
        val other = bitMap.mapValues { 1 }
        val x = other.filterKeys { it.startsWith("x") }.toSortedMap().values.joinToString("").reversed().toLong(radix=2)
        val y = other.filterKeys { it.startsWith("y") }.toSortedMap().values.joinToString("").reversed().toLong(radix=2)
        val z = calculateAll(other)
        return x+y == z
    }

    private fun swapOperationOutput(index1: Int, index2: Int) {
//        operationList[index1].resultName = operationList[index2].resultName
//            .also { operationList[index2].resultName = operationList[index1].resultName }
        val tmp = operationList[index1].resultName
        operationList[index1].resultName = operationList[index2].resultName
        operationList[index2].resultName = tmp
    }

    private fun calculateAll(bitMap: Map<String, Int>): Long {
        val mutBitMap = bitMap.toMutableMap()
        val todo = operationList.toMutableSet()
        while ( todo.isNotEmpty() ) {
            val operation = operationList.firstOrNull { it.canBeCalculated(mutBitMap) } ?: return -1
            operation.execute(mutBitMap)
            todo -= operation
        }
        val resultZ = mutBitMap.filterKeys { it.startsWith("z") }.toSortedMap().values.joinToString("").reversed()
        return resultZ.toLong(radix=2)
    }

}

data class Operator(val operatorType: String, val op1: String, val op2: String, var resultName: String) {

    fun hasBeenCalculated(bitMap: MutableMap<String, Int>) : Boolean {
        return bitMap.contains(resultName)
    }

    fun canBeCalculated(bitMap: MutableMap<String, Int>) : Boolean {
        return bitMap.contains(op1) && bitMap.contains(op2) && !bitMap.contains(resultName)
    }

    fun execute(bitMap: MutableMap<String, Int>) {
        bitMap[resultName] = calculate(operatorType, bitMap[op1]!!, bitMap[op2]!!)
    }

    private fun calculate(operatorType: String, op1: Int, op2: Int): Int {
        return when(operatorType) {
            "AND" -> op1 and op2
            "OR" -> op1 or op2
            "XOR" -> op1 xor op2
            else -> throw Exception("unknown operator")
        }
    }


    override fun toString() = "$op1 $operatorType $op2 -> $resultName"

    companion object {
        fun of(rawInput: String): Operator {
            return Operator(
                operatorType = rawInput.substringAfter(" ").substringBefore(" "),
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


