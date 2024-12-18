package advent

import java.io.File

class AdventApplication

fun main(args: Array<String>) {
    val filePath = "src/main/resources/input.txt"
    val lines = File(filePath).readLines()

    val classObject = DayFifteen(lines)
}