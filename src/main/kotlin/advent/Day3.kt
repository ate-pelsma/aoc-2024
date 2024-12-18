/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

class Day3(lines: List<String>) {
    private val mulRegex: Regex = Regex("mul\\(\\d{1,3},\\d{1,3}\\)")
    private val numberRegex: Regex = Regex("\\d{1,3}")
    private val enableRegex: Regex = Regex("mul\\(\\d{1,3},\\d{1,3}\\)|do\\(\\)|don't\\(\\)")

    init {
        val numberSearch = lines.flatMap {
            mulRegex.findAll(it).map { match ->
                numberRegex.findAll(match.value).map { it.value.toInt() }.toList()
            }
        }.sumOf { it.first() * it.last() }

        val enableSearch = lines.flatMap { enableRegex.findAll(it).map {
            match -> match.value
            }
        }

        countInstruction(enableSearch)
    }

    fun countInstruction(searchList: List<String>) {
        var enabled = true
        val numberList = mutableListOf<List<Int>>()

        for (pattern in searchList) {
            when (pattern) {
                "do()" -> enabled = true
                "don't()" -> enabled = false
                else -> if (enabled) {
                    val numbers = numberRegex.findAll(pattern).map { it.value.toInt() }.toList()
                    numberList.add(numbers)
                }
            }
        }

        println(numberList.sumOf { it.first() * it.last() })
    }
}