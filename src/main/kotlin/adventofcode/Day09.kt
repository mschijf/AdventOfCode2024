package adventofcode

fun main() {
    Day09(test=false).showResult()
}

class Day09(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Disk Fragmenter", hasInputFile = true) {

    private val diskMapDensed = inputLines.first()
        .toCharArray()
        .map{ char -> char.digitToInt() }

    private val freeSpaceIndexed = diskMapDensed
        .mapIndexed { index, i -> FreeSpace(i, if (index >= diskMapDensed.size - 1) -1 else diskMapDensed[index + 1]) }
        .runningFold(FreeSpace(0, 0)) { acc, i -> FreeSpace(acc.index + i.index, i.length) }
        .dropLast(1)
        .filterIndexed { index, _ -> index % 2 == 1 }

    private val fileBlockIndexed = (listOf(0) + diskMapDensed)
        .mapIndexed { index, i -> FileBlock(i, if (index >= diskMapDensed.size) -1 else diskMapDensed[index], index / 2) }
        .runningReduce { acc, i -> FileBlock(acc.index + i.index, i.length, i.id) }
        .filterIndexed { index, _ -> index % 2 == 0 }

    override fun resultPartOne(): Any {
        val diskMap = inputLines.first().explode()
        diskMap.deFragmentize()
        return diskMap.checksum()
    }

    override fun resultPartTwo(): Any {
        fileBlockIndexed.deFragmentizeByBlock()
        return fileBlockIndexed.checksum2()
    }

    private fun String.explode(): MutableList<Int> {
        val explodedList = mutableListOf<Int>()
        this.forEachIndexed { index, c ->
            if (index % 2 == 0) {
                repeat(c.digitToInt()) {
                    explodedList.add(index / 2)
                }
            } else {
                repeat(c.digitToInt()) {
                    explodedList.add(-1)
                }
            }
        }
        return explodedList
    }

    private fun MutableList<Int>.checksum(): Long {
        return this.withIndex().sumOf { if (it.value > -1) it.index * it.value.toLong() else 0 }
    }

    private fun MutableList<Int>.deFragmentize() {
        var lastFilled = this.findPreviousFilled(this.size-1)
        var firstFree = this.findNextEmpty(0)

        while (lastFilled > firstFree) {
            this.swap(firstFree, lastFilled)
            lastFilled = this.findPreviousFilled(lastFilled-1)
            firstFree = this.findNextEmpty(firstFree+1)
        }
    }

    private fun MutableList<Int>.findPreviousFilled(from: Int): Int {
        for (i in from downTo 0) {
            if (this[i] != -1)
                return i
        }
        return -1
    }

    private fun MutableList<Int>.findNextEmpty(from: Int): Int {
        for (i in from..< this.size) {
            if (this[i] == -1)
                return i
        }
        return -1
    }

    private fun MutableList<Int>.swap(index1: Int, index2: Int) {
        this[index1] = this[index2].also { this[index2] = this[index1] }
    }

    private fun List<FileBlock>.deFragmentizeByBlock() {
        this.reversed().forEach { fileBlock ->
            val freeSpaceBlock = freeSpaceIndexed.firstOrNull { fs -> fs.length >= fileBlock.length }
            if (freeSpaceBlock != null && freeSpaceBlock.index < fileBlock.index) {
                fileBlock.index = freeSpaceBlock.index
                freeSpaceBlock.index += fileBlock.length
                freeSpaceBlock.length -= fileBlock.length
            }
        }
    }

    private fun List<FileBlock>.checksum2(): Long {
        var sum = 0L
        this.forEach { fb ->
            repeat(fb.length) { loopIndex ->
                sum += (fb.index + loopIndex) * fb.id
            }
        }
        return sum
    }



}

data class FreeSpace(var index: Int, var length: Int)
data class FileBlock(var index: Int, val length: Int, val id: Int)