package adventofcode

import tool.mylambdas.collectioncombination.filterCombinedItems
import kotlin.math.min

fun main() {
    Day23(test=false).showResult()
}

class Day23(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="LAN Party", hasInputFile = true) {

    private val allConnections = inputLines
        .map { Pair(it.split("-")[0], it.split("-")[1]) }

    private val allComputers = allConnections
        .flatMap { listOf(it.first, it.second) }.distinct()
        .associateWith { Computer(it) }

    init {
        allConnections.forEach { connection ->
            allComputers[connection.first]!!.addConnectedComputer(allComputers[connection.second]!!)
            allComputers[connection.second]!!.addConnectedComputer(allComputers[connection.first]!!)
        }
    }


    override fun resultPartOne(): Any {
//        println("             : 1170")
        return allComputers.flatMap { it.value.strongConnectionGroupsOfThree() }.distinct()
            .filter { it.any { elt -> elt.name.startsWith("t") } }.size
    }


    override fun resultPartTwo(): Any {
//        println("             : bo,dd,eq,ik,lo,lu,ph,ro,rr,rw,uo,wx,yg")
        val result = allComputers.values.map { Pair(it.name, it.biggestStrongConnectedGroup()) }
        val max = result.maxOf { it.second }
        return result.filter { it.second == max }.sortedBy { it.first }.joinToString(",") { it.first }
    }

}

data class Computer(val name: String) {
    val connections = mutableSetOf<Computer>()

    fun addConnectedComputer(otherComputer: Computer) {
        connections.add(otherComputer)
    }

    override fun toString() = name

    fun strongConnectionGroupsOfThree() : List<Set<Computer>> {
        return connections.toList()
            .filterCombinedItems { cn1, cn2 -> cn1 in cn2.connections }
            .map{ setOf(this, it.first, it.second)}
    }

    fun biggestStrongConnectedGroup(): Int {
//        val connectionGroupPerConnection = connections.map { cn -> Pair(cn, (cn.connections.toSet() + cn)) }
//        val yy = connections.map { cn -> connectionGroupPerConnection.filter { cn in it.second }.map { it.first }.toSet() }
//        val zz = yy.groupingBy { it }.eachCount()
//        val bv = zz.maxBy { it.value }
//        return min(bv.key.size, bv.value) + 1
        val connectionGroupPerConnection = connections.map { cn -> (cn.connections.toSet() + cn)  }
        val yy = connections.map { cn -> connectionGroupPerConnection.count{ cn in it } }
        val zz = yy.groupingBy { it }.eachCount()
        val bv = zz.maxBy { it.value }
        return min(bv.key, bv.value) + 1
    }

}
