package ua.helpme.models

class Path(val from: Node, val to: Node, var length: Int, val routes: MutableList<Route> = mutableListOf())