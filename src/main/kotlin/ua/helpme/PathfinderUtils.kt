package ua.helpme

import kotlinx.coroutines.*
import ua.helpme.models.Path
import ua.helpme.models.Route

private val routesComparator = object : Comparator<Route> {
    override fun compare(o1: Route?, o2: Route?): Int {
        val o1steps = o1!!.steps.iterator()
        val o2steps = o2!!.steps.iterator()
        while (o1steps.hasNext() && o2steps.hasNext()) {
            val step1 = o1steps.next()
            val step2 = o2steps.next()

            val diff = step1.index - step2.index
            if (diff != 0) return diff
        }
        return 0
    }
}
private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

suspend fun MutableList<Path>.sortPaths(sortRoutesAsync: Boolean) {
    this.sortWith(Comparator { o1, o2 ->
        val fromDiff = o1!!.from.index - o2!!.from.index
        return@Comparator if (fromDiff != 0) fromDiff else o1.to.index - o2.to.index
    })

    if (sortRoutesAsync) {
        val jobs = ArrayList<Job>(this.size)

        for (path in this) {
            jobs.add(coroutineScope.launch {
                path.routes.sortWith(routesComparator)
            })
        }

        jobs.joinAll()
    } else {
        for (path in this) {
            path.routes.sortWith(routesComparator)
        }
    }
}

