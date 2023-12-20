package ua.helpme

import kotlinx.coroutines.*
import ua.helpme.models.Path

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

