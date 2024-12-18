/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent

class DayFive(lines: List<String>) {
    private val splitIndex = lines.indexOfFirst { it.isBlank() }
    private val orderRulesList = lines.subList(0, splitIndex)
        .filter { it.isNotBlank() }
        .map { it.split("|") }
        .map { OrderRule(it.first(), it.last()) }
    private val pageNumbersList = lines.subList(splitIndex + 1, lines.size)
        .filter { it.isNotBlank() }
        .map { it.split(",").map(String::trim) }

    init {
        processPages()
    }

    data class OrderRule(val first: String, val last: String) {
        fun isIncorrectOrder(pageNumbers: List<String>): Boolean {
            return appliesToPageNumbers(pageNumbers) && isWrongOrder(pageNumbers)
        }

        private fun appliesToPageNumbers(pageNumbers: List<String>): Boolean {
            return pageNumbers.contains(first) && pageNumbers.contains(last)
        }

        private fun isWrongOrder(pageNumbers: List<String>): Boolean {
            return pageNumbers.indexOf(first) > pageNumbers.indexOf(last)
        }
    }

    private fun processPages() {
        println(pageNumbersList.sumOf { checkOrderRules(it) })
        println(pageNumbersList.sumOf { findIncorrectPageNumbers(it) })
    }

    private fun checkOrderRules(pageNumbers: List<String>): Int {
        return if (orderRulesList.any { it.isIncorrectOrder(pageNumbers) }) 0
        else pageNumbers[(pageNumbers.size - 1) / 2].toInt()
    }

    private fun findIncorrectPageNumbers(pageNumbers: List<String>): Int {
        if (orderRulesList.any { it.isIncorrectOrder(pageNumbers) }) {
            val correctedList = fixIncorrectPageNumbers(pageNumbers.toMutableList())
            return correctedList[(correctedList.size - 1) / 2].toInt()
        }

        return 0
    }

    private fun fixIncorrectPageNumbers(pageNumbers: MutableList<String>): List<String> {
        orderRulesList.forEach() { orderRule ->
            if (orderRule.isIncorrectOrder(pageNumbers)) {
                pageNumbers.swap(orderRule.first, orderRule.last)
            }
        }

        return if (orderRulesList.none { it.isIncorrectOrder(pageNumbers) }) {
            pageNumbers
        } else {
            fixIncorrectPageNumbers(pageNumbers)
        }
    }

    private fun MutableList<String>.swap(first: String, last: String) {
        val firstIndex = indexOf(first)
        val lastIndex = indexOf(last)
        if (firstIndex != -1 && lastIndex != -1) {
            this[firstIndex] = this[lastIndex].also { this[lastIndex] = this[firstIndex] }
        }
    }
}