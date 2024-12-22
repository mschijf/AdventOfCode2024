package adventofcode

fun main() {
    Day22(test=false).showResult()
}

class Day22(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Monkey Market", hasInputFile = true) {

    override fun resultPartOne(): Any {
        return inputLines.sumOf { it.toLong().nextSecret(2000) }
    }

    override fun resultPartTwo(): Any {
        val total = mutableListOf<Pair<String, Int>>()
        inputLines("example2").forEach {
            val digitPriceList = it.toLong().generateSecretPriceList(2000)
            val changesList = digitPriceList.createChangeInPricesList()
            val pricePerChangeSequenceList = changesList.createPricePerChangeSequenceList()
            total.addAll(pricePerChangeSequenceList)
        }
        val result = total.groupBy ({ it.first }, { it.second }).mapValues { it.value.sum() }
        return result.maxBy { it.value }.value
    }

    private fun Long.nextSecret(n: Int = 1): Long {
        var secret = this
        repeat(n) {
            val firstRuleResult = ((secret * 64L) xor secret) % 16777216
            val secondRuleResult = ((firstRuleResult / 32L) xor firstRuleResult) % 16777216
            val thirdRuleResult = ((secondRuleResult * 2048L) xor secondRuleResult) % 16777216
            secret = thirdRuleResult
        }
        return secret
    }

    private fun Long.generateSecretPriceList(sequenceLength: Int): List<Int> {
        var secret = this
        val result = mutableListOf(secret.toInt() % 10)
        repeat(sequenceLength) {
            secret = secret.nextSecret()
            val digit = (secret % 10)
            result.add(digit.toInt())
        }
        return result
    }

    private fun List<Int>.createChangeInPricesList(): List<Pair<Int, Int>> {
        return this.zipWithNext {a, b -> Pair(b, b-a) }
    }

    private fun List<Pair<Int, Int>>.createPricePerChangeSequenceList(): List<Pair<String, Int>> {
        return this
            .windowed(4, 1)
            .map { window ->
                Pair(window.joinToString("") { it.second.toString() }, window.last().first)
            }
            .groupBy { it.first }
            .mapValues { it.value.first().second }
            .toList()
    }

}


