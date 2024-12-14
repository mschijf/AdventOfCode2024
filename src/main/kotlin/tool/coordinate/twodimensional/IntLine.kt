package tool.coordinate.twodimensional

data class IntLine(val from: Point, val to: Point) {
    val isHorizontal = from.y == to.y
    val isVertical = from.x == to.x
    fun length() = from.distanceTo(to)
}



