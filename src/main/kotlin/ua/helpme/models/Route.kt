package ua.helpme.models

class Route(val steps: MutableList<Node>) {
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
}