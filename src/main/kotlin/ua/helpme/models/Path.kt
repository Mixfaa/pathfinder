package ua.helpme.models

import kotlinx.coroutines.*
import java.io.PrintStream

class Path(val from: Node, val to: Node, var length: Int, val routes: MutableList<Route> = mutableListOf()) : Comparable<Path>
{
    override fun compareTo(other: Path): Int {
        val fromDiff = this.from.index - other.from.index
        return if (fromDiff != 0) fromDiff else this.to.index - other.to.index
    }
}

fun List<Path>.printPathsTo(printStream: PrintStream) {
    for (path in this) {
        for (route in path.routes) {

            printStream.println("========================================")
            printStream.println("Path: ${path.from.name} -> ${path.to.name}")

            printStream.print("Route: ")
            for ((index, step) in route.steps.withIndex()) {
                if (index != route.steps.lastIndex)
                    printStream.print("${step.name} -> ")
                else
                    printStream.println(step.name)
            }

            printStream.print("Distance: ")

            if (route.steps.size == 2)
                printStream.println(path.length)
            else {
                var prevStep: Node? = null
                for ((index, step) in route.steps.withIndex()) {
                    if (prevStep != null) {
                        if (index != route.steps.lastIndex)
                            printStream.print("${prevStep.getConnectionTo(step)!!.length} + ")
                        else
                            printStream.print(prevStep.getConnectionTo(step)!!.length)
                    }

                    prevStep = step
                }
                printStream.print(" = ${path.length}\n")

            }
            printStream.println("========================================")
        }
    }
}


private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

suspend fun MutableList<Path>.sortPaths(sortRoutesAsync: Boolean) {
    this.sort()

    if (sortRoutesAsync) {
        val jobs = ArrayList<Job>(this.size)

        for (path in this) {
            jobs.add(coroutineScope.launch {
                path.routes.sort()
            })
        }

        jobs.joinAll()
    } else {
        for (path in this)
            path.routes.sort()
    }
}

