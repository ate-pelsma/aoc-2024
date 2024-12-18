/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File

@SpringBootApplication
class AdventApplication

fun main(args: Array<String>) {
    runApplication<AdventApplication>(*args)

    val filePath = "src/main/resources/input.txt"
    val lines = File(filePath).readLines()

    val classObject = DayFifteen(lines)
}