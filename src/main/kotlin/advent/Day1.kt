/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

import kotlin.math.absoluteValue

class Day1(lines: List<String>) {
    private val firstList = mutableListOf<Int>()
    private val secondList = mutableListOf<Int>()

    init {
        val regex = "\\d+".toRegex()
        val numbers = lines.flatMap { line ->
            regex.findAll(line).map { it.value.toInt() }.toList()
        }

        for (i in numbers.indices) {
            if (i % 2 == 0) {
                firstList.add(numbers[i])
            } else {
                secondList.add(numbers[i])
            }
        }

        firstList.sort()
        secondList.sort()
    }

    fun calculateDifference(): Int {
        var total = 0
        for (i in firstList.indices) {
            total += (firstList[i] - secondList[i]).absoluteValue
        }

        return total
    }

    fun calculateSimilarity(): Int {
        var total = 0
        for (number in firstList) {
            val count = secondList.count { it == number }
            total += count * number
        }

        return total
    }
}