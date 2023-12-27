package ua.helpme

import kotlinx.coroutines.runBlocking
import kotlin.io.path.toPath

fun main(args: Array<String>) = runBlocking {
    val executableName = Unit::class.java.protectionDomain
        .codeSource
        .location
        .toURI().toPath().fileName.toString()

    if (args.size != 1) {
        System.err.println("usage: java -jar $executableName [filename]")
        return@runBlocking
    }

    val graphResult = PathfinderParser.parseGraphFromFile(args[0])

    if (graphResult.isFailure) {
        System.err.println(graphResult.exceptionOrNull()!!.localizedMessage)
        return@runBlocking
    }

    val graph = graphResult.getOrNull()!!
    val paths = graph.findAllPaths()

    paths.sortPaths(false)
    paths.printPathsTo(System.out)
}
