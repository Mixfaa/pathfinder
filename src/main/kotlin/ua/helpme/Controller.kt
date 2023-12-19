package ua.helpme

import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.ByteArrayOutputStream
import java.io.PrintStream

@RestController
class Controller {
    @GetMapping("/find_routes")
    fun findRoutes(content: String): String {
        return runBlocking {

            val result = PathfinderParser.parseGraph(content)

            if (result.isFailure)
                return@runBlocking result.exceptionOrNull()!!.message!!
            val graph = result.getOrNull()!!

            val pathsList = graph.findAllPaths()

            pathsList.sortPaths(pathsList.size > 25)

            return@runBlocking  String(ByteArrayOutputStream().use { byteArrayOutputStream ->
                PrintStream(byteArrayOutputStream).use { printStream ->
                    pathsList.printPathsTo(printStream)
                }
                byteArrayOutputStream
            }.toByteArray())
        }
    }
}