package ua.helpme

import ua.helpme.models.Graph
import ua.helpme.models.Node
import ua.helpme.models.NodeConnection
import java.io.FileNotFoundException
import java.io.FileReader

open class PriorityDependentException(val priority: Int, message: String) : Exception(message)

class InvalidNumberOfIslands : PriorityDependentException(1, "error: invalid number of islands")
class DuplicateBridges : PriorityDependentException(2, "error: duplicate bridges")
class InvalidSumOfBridgesLen : PriorityDependentException(3, "error: sum of bridges lengths is too big")

class IdGenerator {
    private var lastId = 0
    fun get() = lastId++
    fun reset() {
        lastId = 0
    }
}

private fun findOrCreateNodeAndAddToList(name: String, idGenerator: IdGenerator, list: MutableList<Node>): Node {
    val node = list.firstOrNull { it.name == name }
    if (node != null)
        return node

    val newNode = Node(name, idGenerator.get())
    list.add(newNode)
    return newNode
}

object PathfinderParser {

    private fun parseLine(line: String): Triple<String, String, Long>? {
        val namesAndLen = line.split(',')
        if (namesAndLen.size != 2) return null

        val names = namesAndLen[0].split('-')
        if (names.size != 2) return null;

        val length = namesAndLen[1].toLongOrNull()
            ?: return null

        if (length <= 0) return null

        if (names[0].length == names[1].length) { // kind of optimization
            var equals = true
            for (index in names[0].indices) {
                val char1 = names[0][index]
                val char2 = names[1][index]

                if (!char1.isLetter() || !char2.isLetter())
                    return null

                if (char1 != char2 && equals)
                    equals = false

                if (index == names[0].lastIndex && equals)
                    return null
            }

            return Triple(names[0], names[1], length)
        }
        if (names[0] == names[1]) return null
        if (!names[0].all { it.isLetter() } || !names[1].all { it.isLetter() }) return null

        return Triple(names[0], names[1], length)
    }

    private fun parseContent(content: String): Result<Graph> {
        val priorityDependentExceptions = mutableListOf<PriorityDependentException>()
        val nodes = mutableListOf<Node>()
        val connections = mutableListOf<NodeConnection>()
        val idGenerator = IdGenerator()

        var expectedNodesCount = 0
        val lines = content.trim().split('\n')
        for ((index, line) in lines.withIndex()) {
            if (index == 0) {
                val nodesCount = line.toIntOrNull()
                    ?: return Result.failure(Throwable("error: line 1 is not valid"))
                expectedNodesCount = nodesCount
                continue
            }


            val (node1Name, node2Name, length) = parseLine(line)
                ?: return Result.failure(Throwable("error: line ${index + 1} is not valid"))

            if (connections.any { it.isBetweenNodesWithNames(node1Name, node2Name) }) {
                priorityDependentExceptions.add(DuplicateBridges())
                continue
            }

            val node1 = findOrCreateNodeAndAddToList(node1Name, idGenerator, nodes)
            val node2 = findOrCreateNodeAndAddToList(node2Name, idGenerator, nodes)

            val nodeConnection = NodeConnection(node1, node2, length)
            connections.add(nodeConnection)

            node1.connections.add(nodeConnection)
            node2.connections.add(nodeConnection)
        }

        if (nodes.size != expectedNodesCount)
            priorityDependentExceptions.add(InvalidNumberOfIslands())

        if (connections.sumOf { it.length } >= Int.MAX_VALUE)
            priorityDependentExceptions.add(InvalidSumOfBridgesLen())

        if (priorityDependentExceptions.isEmpty())
            return Result.success(Graph(nodes, connections))
        return Result.failure(priorityDependentExceptions.minBy { it.priority })
    }

    fun parseGraphFromFile(filename: String): Result<Graph> {
        val fileReader =
            try {
                FileReader(filename)
            } catch (ex: FileNotFoundException) {
                return Result.failure(Throwable("error: file $filename does not exist"))
            }

        return fileReader.use { reader ->
            val content = reader.readText()

            if (content.isEmpty()) return Result.failure(Throwable("error: file $filename is empty"))

            parseContent(content)
        }
    }

    fun parseGraph(content: String): Result<Graph> {
        if (content.isEmpty()) return Result.failure(Throwable("error: file is empty"))
        return parseContent(content)
    }
}

/*
 fun parseGraph(filename: String): Result<Graph> {
        val fileReader =
            try {
                FileReader(filename)
            } catch (ex: FileNotFoundException) {
                return Result.failure(Exception("error: file $filename does not exist"))
            }

        val connectionRegex = Regex("^([A-Za-z]+)-([A-Za-z]+),(\\d+)\$")

        val priorityDependentExceptions = ArrayList<PriorityDependentException>()
        val nodes = ArrayList<Node>()
        val connections = ArrayList<NodeConnection>()
        val nodeIdGenerator = IdGenerator()

        fileReader.use { reader ->
            val lines = reader.readLines()
            if (lines.isEmpty()) return Result.failure(Exception("error: file $filename is empty"))

            var expectedNodesCount = 0
            var index = -1
            while (index < (lines.size - 1)) {
                ++index
                val line = lines[index]
                if (index == 0) {
                    val nodesCount = line.toIntOrNull()
                        ?: return Result.failure(Exception("error: line 1 is not valid"))
                    expectedNodesCount = nodesCount
                    continue
                }

                if (line.isBlank()) continue

                if (connectionRegex.matches(line)) {
                    val (node1Name, node2Name, lengthString) = connectionRegex.find(line)!!.destructured
                    val length = lengthString.toLongOrNull()
                        ?: return Result.failure(Exception("error: line ${index + 1} is not valid"))

                    if (connections.any { it.isBetweenNodesWithNames(node1Name, node2Name) }) {
                        priorityDependentExceptions.add(DuplicateBridges())
                        continue
                    }

                    val node1 = findOrCreateNodeAndAddToList(node1Name, nodeIdGenerator, nodes)
                    val node2 = findOrCreateNodeAndAddToList(node2Name, nodeIdGenerator, nodes)

                    val nodeConnection = NodeConnection(node1, node2, length)
                    connections.add(nodeConnection)

                    node1.connections.add(nodeConnection)
                    node2.connections.add(nodeConnection)

                } else
                    return Result.failure(Exception("error: line ${index + 1} is not valid"))

            }


            if (nodes.size != expectedNodesCount)
                priorityDependentExceptions.add(InvalidNumberOfIslands())

            if (connections.sumOf { it.length } >= Int.MAX_VALUE)
                priorityDependentExceptions.add(InvalidSumOfBridgesLen())
        }

        if (priorityDependentExceptions.isEmpty())
            return Result.success(Graph(nodes, connections))
        return Result.failure(priorityDependentExceptions.minBy { it.priority })
    }
 */