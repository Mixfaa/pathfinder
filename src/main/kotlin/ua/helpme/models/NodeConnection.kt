package ua.helpme.models

class NodeConnection(val node1: Node, val node2: Node, val length: Long) {
    fun isBetweenNodesWithNames(name1: String, name2: String): Boolean {
        return (node1.name == name1 &&
                node2.name == name2) ||
                (node2.name == name1 &&
                        node1.name == name2)
    }

    fun otherNode(node: Node): Node {
        return if (node1 == node) node2 else node1
    }

}