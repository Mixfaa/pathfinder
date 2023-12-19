package ua.helpme

import ua.helpme.models.Node
import ua.helpme.models.Path
import java.io.PrintStream

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


