/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent

class DayTen(lines: List<String>) {

    class GridPoint(val x: Int, val y: Int, val height: Int, val isTrailHead: Boolean) {
        var isIntersection = false
        var hasBeenReached = false
        var timesReached: Int = 0
        var reachedEnds: Int = 0
        var rating: Int = 0
        var surroundingPoints = mutableListOf<GridPoint>()

        fun addSurroundingPoints() {
            val surroundingIndexes = listOf(
                x - 1 to y,
                x + 1 to y,
                x to y - 1,
                x to y + 1
            )

            surroundingIndexes.forEach { (x, y) ->
                gridPointList.find { it.x == x && it.y == y }?.let {
                    surroundingPoints.add(it)
                }
            }
        }

        fun checkNextPoint() {
            if (isIntersectionPoint()) {
                isIntersection = true
            }
        }

        fun isIntersectionPoint(): Boolean {
            return surroundingPoints.filter { it.height == height + 1 }.size > 1
        }

        fun nextPoint(): List<GridPoint> {
            return surroundingPoints.filter { it.height == height + 1 }
        }

        override fun toString(): String {
            val surroundingPointsStr = surroundingPoints.map { "(${it.x}, ${it.y}, ${it.height})" }
            return "GridPoint(x=$x, y=$y, height=$height, $isTrailHead ,$surroundingPointsStr)"
        }
    }

    companion object {
        lateinit var gridPointList: List<GridPoint>
    }

    init {
        gridPointList = lines.flatMapIndexed { rowIndex, line ->
            line.mapIndexed { colIndex, char ->
                GridPoint(rowIndex, colIndex, if (char != '.') char.toString().toInt() else -1, char == '0')
            }
        }

        gridPointList.forEach { gridPoint ->
            gridPoint.addSurroundingPoints()
            gridPoint.checkNextPoint()
        }

        startTraversing()
        calculateAndPrintPuzzleScores()
    }

    fun startTraversing() {
        for (trailHead in findTrailHeads()) {
            resetGridPointList()
            traversePoint(trailHead)
            setTrailHeadScores(trailHead)
        }
    }

    fun findTrailHeads(): List<GridPoint> {
        return gridPointList.filter { it.isTrailHead }
    }

    fun findEndpoints(): List<GridPoint> {
        return gridPointList.filter { it.height == 9 }
    }

    fun resetGridPointList() {
        gridPointList.forEach { it.hasBeenReached = false; it.timesReached = 0 }
    }

    fun setTrailHeadScores(trailHead: GridPoint) {
        trailHead.reachedEnds = calculateReachedEnds()
        trailHead.rating = calculateRating()
    }

    fun calculateReachedEnds(): Int {
        return findEndpoints().filter { it.hasBeenReached }.size
    }

    fun calculateRating(): Int {
        return findEndpoints().filter { it.hasBeenReached }.sumOf { it.timesReached }
    }

    fun traversePoint(point: GridPoint) {
        val nextPoints = point.nextPoint()
        nextPoints.forEach { nextPoint ->

            if (nextPoint.hasBeenReached) {
                nextPoint.timesReached++
                return@forEach
            }

            if (nextPoint.height == 9) {
                nextPoint.timesReached++
                nextPoint.hasBeenReached = true
                return@forEach
            }

            traversePoint(nextPoint)
        }
    }

    fun calculateAndPrintPuzzleScores() {
        println("Total trailhead score: ${findTrailHeads().sumOf { it.reachedEnds }}")
        println("Total trailhead rating: ${findTrailHeads().sumOf { it.rating }}")
    }
}