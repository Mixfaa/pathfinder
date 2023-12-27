package ua.helpme.models

class NodeCost(
    val node: Node,
    var cost: Int,
    var visited: Boolean,
    val routesToMe: MutableList<Route> = ArrayList()
) {
    fun routesToPaths(): List<Path> {
        val grouped = routesToMe.groupBy { it.steps.first() }
        val paths = mutableListOf<Path>()
        for ((start, routes) in grouped) {
            val path = Path(start, node, cost)
            path.routes.addAll(routes)
            paths.add(path)
        }
        return paths
    }
}

class Graph(private val nodes: List<Node>, val connections: List<NodeConnection>) {

    private fun initCostsForStartNode(startNode: Node): List<NodeCost> {
        val nodeCosts = ArrayList<NodeCost>()

        for (node in nodes)
            nodeCosts.add(
                if (node == startNode) NodeCost(node, 0, false, mutableListOf(Route(node))) else NodeCost(
                    node,
                    Int.MAX_VALUE,
                    false
                )
            )

        return nodeCosts
    }

    private fun mergePathsFromCosts(pathList: MutableList<Path>, costs: List<NodeCost>) {

        val paths = mutableListOf<Path>()
        for (cost in costs)
            paths.addAll(cost.routesToPaths())

        for (newPath in paths) {
            if (newPath.from == newPath.to)
                continue

            if (pathList.any { it.from == newPath.to && it.to == newPath.from })
                continue

            val samePath = pathList.firstOrNull { it.from == newPath.from && it.to == newPath.to }
            if (samePath == null) {
                pathList.add(newPath)
                continue
            }

            if (samePath.length > newPath.length) {
                samePath.routes.clear()
                samePath.routes.addAll(newPath.routes)
                samePath.length = newPath.length
                continue
            }

            if (samePath.length == newPath.length) {
                samePath.routes.addAll(newPath.routes)
                continue
            }
        }
    }


    fun findAllPaths(): MutableList<Path> {
        val pathList = ArrayList<Path>(this.nodes.size * this.nodes.size) // from each node to each

        for (startNode in nodes) {
            val costsList = initCostsForStartNode(startNode)

            while (true) {
                val currentNodeCost = costsList.filter { !it.visited }.minByOrNull { it.cost }
                    ?: break // no unvisited node
                currentNodeCost.visited = true

                for (connection in currentNodeCost.node.connections) {
                    val otherNode = connection.otherNode(currentNodeCost.node)
                    val otherNodeCost = costsList.firstOrNull { it.node == otherNode }
                        ?: throw Exception("Cost not found in list for node $otherNode")

                    val newCost = (currentNodeCost.cost + connection.length).toInt()

                    if (newCost <= otherNodeCost.cost) {

                        if (newCost < otherNodeCost.cost) {
                            otherNodeCost.routesToMe.clear()
                            otherNodeCost.cost = newCost
                        }

                        for (route in currentNodeCost.routesToMe)
                            otherNodeCost.routesToMe.add(route.copyWithFinalStep(otherNode))
                    }
                }
            }
            mergePathsFromCosts(pathList, costsList)
        }
        return pathList
    }
}