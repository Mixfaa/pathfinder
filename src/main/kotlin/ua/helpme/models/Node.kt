package ua.helpme.models

class Node(val name: String, val index: Int, val connections: MutableList<NodeConnection> = ArrayList()) {
    fun getConnectionTo(otherNode: Node): NodeConnection? {
        for (connection in connections) {
            if (connection.otherNode(this) == otherNode)
                return connection
        }
        return null
    }
}