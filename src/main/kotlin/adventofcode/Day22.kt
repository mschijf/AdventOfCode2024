package adventofcode

fun main() {
    Day22(test=false).showResult()
}

class Day22(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    override fun resultPartOne(): Any {
        return inputLines.sumOf { it.toLong().next2000Secret() }
    }

    override fun resultPartTwo(): Any {
        val total = mutableListOf<Pair<String, Int>>()
        inputLines("example2").forEach {
            val digitList = it.toLong().generateSecretSequence(2000)
            val changesList = digitList.generateChanges()
            val changeSequences = changesList.generateChangeSequences()
            val sequenceToFirstPrice = changeSequences.sequenceToPrice()
            total.addAll(sequenceToFirstPrice)
        }
        val result = total.groupBy { it.first }.mapValues { it -> it.value.sumOf {pair -> pair.second} }
        return result.values.max()
    }

    private fun Long.nextSecret(): Long {
        val secret = this
        val firstRuleResult = ( (secret * 64L) xor secret ) % 16777216
        val secondRuleResult = ( (firstRuleResult/32L) xor firstRuleResult ) % 16777216
        val thirdRuleResult = ( (secondRuleResult*2048L) xor secondRuleResult ) % 16777216
        return thirdRuleResult
    }

    private fun Long.next2000Secret(): Long {
        var secret = this
        repeat(2000) {
            secret = secret.nextSecret()
        }
        return secret
    }

    private fun Long.generateSecretSequence(n: Int): List<Int> {
        var secret = this
        val result = mutableListOf<Int>(secret.toInt() % 10)
        repeat(n) {
            secret = secret.nextSecret()
            val digit = (secret % 10)
            result.add(digit.toInt())
        }
        return result
    }

    private fun List<Int>.generateChanges(): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        for (i in 1..<this.size) {
            result.add(Pair (this[i], this[i]-this[i-1]))
        }
        return result
    }

    private fun List<Pair<Int, Int>>.generateChangeSequences(): List<Pair<String, Int>> {
        return this
            .windowed(4, 1)
            .map { window ->
                Pair(window.joinToString("") { it.second.toString() }, window.last().first)
            }
    }

    private fun List<Pair<String, Int>>.sequenceToPrice(): List<Pair<String, Int>> {
        return this.groupBy { it.first }.mapValues { it.value.first().second }.toList()
    }
}


