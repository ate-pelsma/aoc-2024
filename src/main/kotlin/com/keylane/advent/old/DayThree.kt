/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent.old

class DayThree(private val grid: List<String>) {
    private val numberRegex = "\\d+".toRegex()
    private val gearRegex = Regex.fromLiteral("*")
    private val partNumbers: List<Int>
    private var gearNumbers: List<List<Int>> = mutableListOf()

    init {
        partNumbers = findAll(numberRegex)
            .filter { isBoundedBySpecialChars(it) }
            .map { (_, match) -> match.value.toInt() }

        gearNumbers = findAll(gearRegex)
            .map { getGearsBoundedByTwoNumbers(it) }
            .filter { it.isNotEmpty() && it.size == 2 }
    }

    private fun findAll(regex: Regex): List<Pair<Int, MatchResult>> = grid.flatMapIndexed { index, s -> regex.findAll(s).map { index to it } }

    private fun isBoundedBySpecialChars(indexMatch: Pair<Int, MatchResult>): Boolean {
        val (rowRange, colRange) = getBoundingRectangle(indexMatch)
        return rowRange.any { row -> colRange.any { col -> isSpecialChar(grid[row][col]) } }
    }

    private fun getBoundingRectangle(indexMatch: Pair<Int, MatchResult>): Pair<IntRange, IntRange> {
        val (index, match) = indexMatch
        val rowRange = ((index - 1)..(index + 1)).filter { row -> row in grid.indices }
        val colRange = ((match.range.first - 1)..(match.range.last + 1)).filter { col -> col in grid[0].indices }
        return rowRange.asIntRange() to colRange.asIntRange()
    }

    private fun isSpecialChar(char: Char) = !char.isDigit() && char != '.'

    private fun getGearsBoundedByTwoNumbers(indexMatch: Pair<Int, MatchResult>): List<Int> {
        val (rowGear, columnGear) = getIndexPositionGear(indexMatch)
        return getOverlappingNumbers(rowGear, columnGear)
    }

    private fun getIndexPositionGear(indexMatch: Pair<Int, MatchResult>): Pair<Int, Int> {
        val (index, match) = indexMatch
        return index to match.range.first
    }

    private fun getOverlappingNumbers(rowGear: Int, columnGear: Int): List<Int> {
        val numberList: MutableList<Int> = mutableListOf()
        findAll(numberRegex).forEach { (index, match) ->
            val (rowRange, colRange) = getBoundingRectangle(index to match)
            if (rowGear in rowRange && columnGear in colRange) {
                numberList.add(match.value.toInt())
            }
        }

        return numberList;
    }

    public fun sumOfPartNumbers() = partNumbers.sum()

    public fun sumOfGearNumbers() = gearNumbers.sumOf { it[0] * it[1] }
}

private fun List<Int>.asIntRange() = this.first() .. this.last()