package ua.helpme.models

class Route(val steps: MutableList<Node>) : Comparable<Route>{
    constructor(node: Node) : this(mutableListOf(node))

    fun copyWithFinalStep(step: Node): Route {
        val newSteps = ArrayList(this.steps)
        newSteps.add(step)

        return Route(newSteps)
    }

    fun cost(): Int {
        var prevStep: Node? = null
        var totalLength = 0
        for (step in steps) {
            if (prevStep != null)
                totalLength += prevStep.getConnectionTo(step)!!.length.toInt()

            prevStep = step
        }
        return totalLength
    }

    override fun compareTo(other: Route): Int {
        val o1steps = this.steps.iterator()
        val o2steps = other.steps.iterator()
        while (o1steps.hasNext() && o2steps.hasNext()) {
            val step1 = o1steps.next()
            val step2 = o2steps.next()

            val diff = step1.index - step2.index
            if (diff != 0) return diff
        }
        return 0
    }
}