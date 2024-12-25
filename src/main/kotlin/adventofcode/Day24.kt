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
        return "No solution yet"
    }

    private fun makeOutputList(indexList: List<Int>): String {
        return indexList.map {idx -> operationList[idx].resultName}.sorted().joinToString(",")
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



