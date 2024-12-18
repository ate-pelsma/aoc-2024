/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

class Day4(lines: List<String>) {
    private val puzzleInput: List<String> = lines

    init {
        processPuzzle()
        checkXMas()
    }

    fun processPuzzle() {
        var count = 0

        for (row in puzzleInput.indices) {
            for (column in puzzleInput[row].indices) {
                if (puzzleInput[row][column] == 'X') {
                    val puzzleRow = puzzleInput[row]
                    if (isHorizontal(puzzleRow, column)) {
                        count++
                    }
                    if (isBackwards(puzzleRow, column)) {
                        count++
                    }
                    if (isVertical(row, column)) {
                        count++
                    }
                    if (isVerticalUp(row, column)) {
                        count++
                    }
                    if (isDiagonalDownRight(row, column, "XMAS", 3)) {
                        count++
                    }
                    if (isDiagonalDownLeft(row, column, "XMAS", 3)) {
                        count++
                    }
                    if (isDiagonalUpRight(row, column, "XMAS", 3)) {
                        count++
                    }
                    if (isDiagonalUpLeft(row, column, "XMAS", 3)) {
                        count++
                    }
                }
            }
        }

        println(count)
    }

    fun checkXMas() {
        var count = 0
        for (row in puzzleInput.indices) {
            for (column in puzzleInput[row].indices) {
                if (isValidBox(row, column)) {
                    if (isXMasBox(row, column)) {
                        count++
                    }
                }
            }
        }

        println(count)
    }

    fun isValidBox(row: Int, column: Int): Boolean {
        return row + 2 < puzzleInput.size && column + 2 < puzzleInput[row].length
    }

    fun isXMasBox(row: Int, column: Int): Boolean {
        return (isDiagonalDownRight(row, column, "MAS", 2) || isDiagonalUpLeft(row + 2, column + 2, "MAS", 2))
                && (isDiagonalDownLeft(row, column + 2, "MAS", 2) || isDiagonalUpRight(row + 2, column, "MAS", 2))
    }

    fun isHorizontal(row: String, column: Int): Boolean {
        return row.length >= column + 4 && row.subSequence(column, column + 4) == "XMAS"
    }

    fun isBackwards(row: String, column: Int): Boolean {
        val reversedRow = row.reversed()
        return isHorizontal(reversedRow, reversedRow.length - column - 1)
    }

    fun isVertical(row: Int, column: Int): Boolean {
        if (row + 3 >= puzzleInput.size) {
            return false
        }

        var verticalString = ""
        for (i in 0..3) {
            verticalString += puzzleInput[row + i][column]
        }

        return verticalString == "XMAS"
    }

    fun isVerticalUp(row: Int, column: Int): Boolean {
        if (row - 3 < 0) {
            return false
        }

        var verticalString = ""
        for (i in 0..3) {
            verticalString += puzzleInput[row - i][column]
        }

        return verticalString == "XMAS"
    }

    fun isDiagonalDownRight(row: Int, column: Int, expected: String, indexShift: Int): Boolean {
        if (row + indexShift >= puzzleInput.size || column + indexShift >= puzzleInput[row].length) {
            return false
        }

        var diagonalString = ""
        for (i in 0..indexShift) {
            diagonalString += puzzleInput[row + i][column + i]
        }

        return diagonalString == expected
    }

    fun isDiagonalDownLeft(row: Int, column: Int, expected: String, indexShift: Int): Boolean {
        if (row + indexShift >= puzzleInput.size || column - indexShift < 0) {
            return false
        }

        var diagonalString = ""
        for (i in 0..indexShift) {
            diagonalString += puzzleInput[row + i][column - i]
        }

        return diagonalString == expected
    }

    fun isDiagonalUpRight(row: Int, column: Int, expected: String, indexShift: Int): Boolean {
        if (row - indexShift < 0 || column + indexShift >= puzzleInput[row].length) {
            return false
        }

        var diagonalString = ""
        for (i in 0..indexShift) {
            diagonalString += puzzleInput[row - i][column + i]
        }

        return diagonalString == expected
    }

    fun isDiagonalUpLeft(row: Int, column: Int, expected: String, indexShift: Int): Boolean {
        if (row - indexShift < 0 || column - indexShift < 0) {
            return false
        }

        var diagonalString = ""
        for (i in 0..indexShift) {
            diagonalString += puzzleInput[row - i][column - i]
        }

        return diagonalString == expected
    }
}