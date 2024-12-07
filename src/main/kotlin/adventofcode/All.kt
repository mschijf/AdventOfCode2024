package adventofcode

fun main() {
    val test = false
    (1..25).forEach { dayNr -> runDay(dayNr, test) }
}

fun runDay(dayNr: Int, test: Boolean) {
    val className = "Day%02d".format(dayNr)
    val packageName = "adventofcode"
    try {
        val kClass = Class.forName("$packageName.$className").kotlin
        val method = kClass.members.find { it.name == "showResultShort" }
        val obj = kClass.constructors.first().call(test)
        method!!.call(obj)
    } catch(e: ClassNotFoundException) {
        println("$className not implemented (yet)")
    } catch (otherE: Exception) {
        println("$className runs with exception ${otherE.cause}")
    }
}

