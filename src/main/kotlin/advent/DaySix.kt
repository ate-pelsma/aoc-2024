/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

class DaySix(lines: List<String>) {
    private val maxRowIndex = lines.size - 1
    private val maxColIndex = lines[0].length - 1
    private val arrowRegex: Regex = Regex("[\\^>v<]")
    private val obstacleRegex: Regex = Regex("#")

    private val originalArrowPosition: Pair<Pair<Int, Int>, String>? = lines.flatMapIndexed { index, line ->
        arrowRegex.find(line)?.let { listOf((index to it.range.first) to it.value) } ?: emptyList()
    }.firstOrNull()
    private var arrowPosition = originalArrowPosition
    private val originalObstaclePositions: List<Pair<Int, Int>> = lines.flatMapIndexed { index, line ->
        obstacleRegex.findAll(line).map { index to it.range.first }
    }
    private var obstaclePositions = originalObstaclePositions.toMutableList()
    private val startingPosition = arrowPosition?.first

    private var positionsTaken: MutableList<Pair<Pair<IntRange, IntRange>, String>> = mutableListOf()
    private var uniquePositions = mutableSetOf<Pair<Int, Int>>()

    init {
        processFirstPuzzle()
        processSecondPuzzle()
    }

    private fun processFirstPuzzle() {
        var newPosition = moveArrow()
        while (newPosition != null) {
            arrowPosition = newPosition
            newPosition = moveArrow()
        }

        calculateUniquePositions()
    }

    private fun processSecondPuzzle() {
        val count = uniquePositions.count {
            positionsTaken = mutableListOf()
            arrowPosition = originalArrowPosition

            if (startingPosition != it) {
                addObstacle(it)
            }

            var newPosition = moveArrow()
            while (newPosition != null) {
                arrowPosition = newPosition
                newPosition = moveArrow()

                if (newPosition == null) {
                    return@count false
                }

                if (isInfiniteLoop()) {
                    return@count true
                }
            }

            false
        }

        println(count)
    }

    private fun moveArrow(): Pair<Pair<Int, Int>, String>? {
        val (position, direction) = arrowPosition ?: return null
        val (row, col) = position
        val newPosition = when (direction) {
            "^" -> goUp(row, col, direction)
            ">" -> goRight(row, col, direction)
            "v" -> goDown(row, col, direction)
            "<" -> goLeft(row, col, direction)
            else -> null
        }

        return newPosition
    }

    private fun calculateUniquePositions() {
        uniquePositions.addAll(positionsTaken.flatMap { (pair, _)  ->
            val (rowRange, colRange) = pair
            rowRange.flatMap { row -> colRange.map { col -> row to col } }
        })

        println(uniquePositions.size)
    }

    private fun isInfiniteLoop(): Boolean {
        if (positionsTaken.size <= 4) {
            return false
        }

        return positionsTaken.toSet().size != positionsTaken.size
    }

    private fun goUp(row: Int, col: Int, direction: String): Pair<Pair<Int, Int>, String>? {
        val closestObstacle = obstaclePositions
            .filter { it.second == col && it.first < row }
            .maxByOrNull { it.first }

        return if (closestObstacle != null) {
            val (obstacleRow, _) = closestObstacle
            val newPosition = obstacleRow + 1 to col
            positionsTaken.add((newPosition.first..row to col..col) to direction)
            return newPosition to ">"
        } else {
            positionsTaken.add((0..row to col..col) to direction)
            return null
        }
    }

    private fun goDown(row: Int, col: Int, direction: String): Pair<Pair<Int, Int>, String>? {
        val closestObstacle = obstaclePositions
            .filter { it.second == col && it.first > row }
            .minByOrNull { it.first }

        return if (closestObstacle != null) {
            val (obstacleRow, _) = closestObstacle
            val newPosition = obstacleRow - 1 to col
            positionsTaken.add((row..newPosition.first to col..col) to direction)
            return newPosition to "<"
        } else {
            positionsTaken.add((row..maxRowIndex to col..col) to direction)
            return null
        }
    }

    private fun goRight(row: Int, col: Int, direction: String): Pair<Pair<Int, Int>, String>? {
        val closestObstacle = obstaclePositions
            .filter { it.first == row && it.second > col }
            .minByOrNull { it.second }

        return if (closestObstacle != null) {
            val (_, obstacleCol) = closestObstacle
            val newPosition = row to obstacleCol - 1
            positionsTaken.add((row..row to col..newPosition.second) to direction)
            newPosition to "v"
        } else {
            positionsTaken.add((row..row to col..maxColIndex) to direction)
            null
        }
    }

    private fun goLeft(row: Int, col: Int, direction: String): Pair<Pair<Int, Int>, String>? {
        val closestObstacle = obstaclePositions
            .filter { it.first == row && it.second < col }
            .maxByOrNull { it.second }

        return if (closestObstacle != null) {
            val (_, obstacleCol) = closestObstacle
            val newPosition = row to obstacleCol + 1
            positionsTaken.add((row..row to newPosition.second..col) to direction)
            return newPosition to "^"
        } else {
            positionsTaken.add((row..row to 0..col) to direction)
            return null
        }
    }

    private fun addObstacle(position: Pair<Int, Int>) {
        val newObstacleList = originalObstaclePositions.toMutableList()
        newObstacleList.add(position.first to position.second)
        obstaclePositions = newObstacleList
    }

    fun printGrid(uniquePositions: Set<Pair<Int, Int>>) {
        val gridSize = 10
        val grid = Array(gridSize) { CharArray(gridSize) { '.' } }

        uniquePositions.forEach { (row, col) ->
            if (row in 0 until gridSize && col in 0 until gridSize) {
                grid[row][col] = 'X'
            }
        }

        grid.forEach { row ->
            println(row.joinToString(" "))
        }
    }
}