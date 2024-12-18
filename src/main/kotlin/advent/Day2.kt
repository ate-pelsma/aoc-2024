/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

class Day2(lines: List<String>) {
    private var numberList: List<List<Int>> = lines.map { it.split(" ").map { it.toInt() } }

    init {
        val count = numberList.count { isSafeReport(it) }
        println(count)
        var secondCount = 0
        for (report in numberList) {
            if (isSafeReport(report)) {
                secondCount++
                continue
            } else {
                if (isProblemDampener(report)) {
                    secondCount++
                }
            }
        }

        println(secondCount)
    }

    private fun isSafeReport(numbers: List<Int>): Boolean {
        val isAscending = numbers.zipWithNext().all { (a, b) -> b > a && b - a in 1..3}
        val isDescending = numbers.zipWithNext().all { (a, b) -> b < a && a - b in 1..3}

        return isAscending || isDescending
    }

    private fun isProblemDampener(numbers: List<Int>): Boolean {
        for (i in numbers.indices) {
            if (isSafeReport(numbers.filterIndexed { index, _ -> index != i })) {
                return true
            }
        }

        return false
    }
}