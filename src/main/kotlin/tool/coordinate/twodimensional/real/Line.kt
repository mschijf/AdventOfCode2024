package tool.coordinate.twodimensional.real

// definition of a line: aX + bY = c
data class Line(val a: Double, val b: Double, val c: Double) {
    companion object {
        fun of(p1: Coordinate, p2: Coordinate): Line {
            val a = (p1.y - p2.y) / (p1.x - p2.x)
            return Line(
                a = a,
                b = -1.0,
                c = p1.y - a * p1.x
            )
        }
    }

    fun intersectionOrNull(other: Line): Coordinate? {
        if (other.a == 0.0 || this.a == 0.0)
            return null

        val tmp = this.a / other.a
        if (this.b - other.b * tmp == 0.0)
            return null

        val y = (this.c - other.c * tmp) / (this.b - other.b * tmp)
        val x = (this.c - this.b * y) / this.a
        return Coordinate(x, y)
    }
}

