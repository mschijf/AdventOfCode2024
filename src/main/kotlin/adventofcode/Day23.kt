package adventofcode

import kotlin.math.min

fun main() {
    Day23(test=false).showResult()
}

class Day23(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="LAN Party", hasInputFile = true) {

    private val allConnections = inputLines
        .map { Pair (it.split("-")[0], it.split("-")[1]) }

    private val allComputers = allConnections
        .flatMap { listOf(it.first, it.second) }.distinct()
        .associateWith { Computer(it) }

    override fun resultPartOne(): Any {
        allConnections.forEach { connection ->
            allComputers[connection.first]!!.addConnectedComputer(allComputers[connection.second]!!)
            allComputers[connection.second]!!.addConnectedComputer(allComputers[connection.first]!!)
        }
        println("11440 is not correct")
        println("4004 is too high")
        println("1189 is not correct")
        println("1170 is correct!")
        return checkPart1()
    }

    override fun resultPartTwo(): Any {
        allConnections.forEach { connection ->
            allComputers[connection.first]!!.addConnectedComputer(allComputers[connection.second]!!)
            allComputers[connection.second]!!.addConnectedComputer(allComputers[connection.first]!!)
        }
//        println("vc")
//        checkPart2(allComputers["vc"]!!)
//        println()
//        println("ka")
//        checkPart2(allComputers["ka"]!!)

//        checkPart2(allComputers["ab"]!!)
        val result = allComputers.values.map { Pair(it.name, checkPart2(it)) }
        val max = result.maxOf { it.second }
        return result.filter { it.second == max }.sortedBy { it.first }.joinToString (","){ it.first }
    }

    private fun checkPart1(): Int {
        var count = 0
        val computerList = if (test) allComputers.values.toList().sortedBy { it.name } else allComputers.values.toList()
        for (i in 0..< computerList.size - 2) {
            val setI = computerList[i].connections.toSet() + computerList[i]
            for (j in i+1..< computerList.size - 1) {
                val setJ = computerList[j].connections.toSet() + computerList[j]
                val combinedSet2 = setI intersect setJ
                if (combinedSet2.size >= 3) {
                    for (k in j + 1..<computerList.size) {
                        val setK = computerList[k].connections.toSet() + computerList[k]
                        val combinedSet3 = combinedSet2 intersect setK
                        if (combinedSet3.size >= 3) {
                            if (computerList[i] in combinedSet3 && computerList[j] in combinedSet3 && computerList[k] in combinedSet3) {
                                if (computerList[i].name.startsWith("t") || computerList[j].name.startsWith("t") || computerList[k].name.startsWith("t")) {
                                    count++
                                    if (test)
                                        println("${computerList[i]} ${computerList[j]} ${computerList[k]}")
                                }
                            }
                        }
                    }
                }
            }
        }
        return count
    }

    fun checkPart2(computer: Computer): Int {
        val xx = computer.connections.map { cn1 -> (cn1.connections.toSet() + cn1)  }
        val yy = computer.connections.map { cn1 -> xx.count{ cn1 in it } }
        val zz = yy.groupingBy { it }.eachCount()
        if (false) {
            println(computer.connections)
            println(xx)
            println(yy)
            println(zz)
        }
        val bv = zz.maxBy { it.value }
        return min(bv.key, bv.value) + 1
    }
}

data class Computer(val name: String) {
    val connections = mutableListOf<Computer>()

    fun addConnectedComputer(otherComputer: Computer) {
        connections.add(otherComputer)
    }

    override fun toString() = name
}



//fun printAllCombis(list: List<Char> = listOf('a', 'b', 'c', 'd', 'e')) {
//    if (list.isEmpty()) {
//        print("")
//    } else {
//        print(list[0])
//        printAllCombis(list.drop(1))
//        println()
//    }
//}
//
