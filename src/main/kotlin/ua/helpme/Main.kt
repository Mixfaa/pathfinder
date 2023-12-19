package ua.helpme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringEntryPoint

fun main(args: Array<String>) {
    runApplication<SpringEntryPoint>(*args)
}
