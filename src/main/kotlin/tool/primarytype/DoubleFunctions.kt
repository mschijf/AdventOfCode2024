package tool.primarytype

import kotlin.math.absoluteValue
import kotlin.math.roundToLong

fun Double.isCloseToLong(tolerance: Double = 0.0001): Boolean {
    val nearestLong = this.roundToLong()
    return (this - nearestLong).absoluteValue <= tolerance
}