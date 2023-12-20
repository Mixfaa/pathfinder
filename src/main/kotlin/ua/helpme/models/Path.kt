package ua.helpme.models

class Path(val from: Node, val to: Node, var length: Int, val routes: MutableList<Route> = mutableListOf()) : Comparable<Path>
{
    override fun compareTo(other: Path): Int {
        val fromDiff = this.from.index - other.from.index
        return if (fromDiff != 0) fromDiff else this.to.index - other.to.index
    }
}