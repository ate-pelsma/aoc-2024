/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

import kotlin.math.absoluteValue

class DayEight(lines: List<String>) {
    private val regex = Regex("[a-z]|\\d|[A-Z]")
    private val antennaPoints = lines.flatMapIndexed { index, line -> regex.findAll(line).map { AntennaPoint(it.value, index, it.range.first) } }
    private val antinodePoints = mutableSetOf<Pair<Int, Int>>()
    private val harmonyPoints = mutableSetOf<Pair<Int, Int>>()

    companion object {
        lateinit var gridBounds: Pair<IntRange, IntRange>
    }

    class AntennaPoint(val name: String, val x: Int, val y: Int) {
        var isProcessed = false

        fun antinodePoints(otherAntennaPoint: AntennaPoint): List<Pair<Int, Int>> {
            val rowDiff = (otherAntennaPoint.x - x)
            val colDiff = (otherAntennaPoint.y - y)

            return listOf(x - rowDiff to y - colDiff, x + 2 * rowDiff to y + 2 * colDiff)
                .filter { point -> point.first in gridBounds.first && point.second in gridBounds.second }
        }

        fun harmonyPoints(otherAntennaPoint: AntennaPoint): List<Pair<Int, Int>> {
            val rowDiff = (otherAntennaPoint.x - x)
            val colDiff = (otherAntennaPoint.y - y)
            val antennaPoints = mutableListOf<Pair<Int, Int>>()

            antennaPoints.add(Pair(x, y))
            antennaPoints.add(Pair(otherAntennaPoint.x, otherAntennaPoint.y))

            // Extend the line in both directions
            var multiplier = 1
            var addedPoint: Boolean
            while (true) {
                addedPoint = false

                // Forward direction
                val forwardX = x + multiplier * rowDiff
                val forwardY = y + multiplier * colDiff
                val backwardX = x - multiplier * rowDiff
                val backwardY = y - multiplier * colDiff

                if (forwardX in gridBounds.first && forwardY in gridBounds.second) {
                    antennaPoints.add(Pair(forwardX, forwardY))
                    addedPoint = true
                }

                if (backwardX in gridBounds.first && backwardY in gridBounds.second) {
                    antennaPoints.add(Pair(backwardX, backwardY))
                    addedPoint = true
                }

                if (!addedPoint) {
                    break
                }

                multiplier++
            }


            return antennaPoints.filter { it.first in gridBounds.first && it.second in gridBounds.second }
        }

        override fun toString(): String {
            return "AntennaPoint(name='$name', x=$x, y=$y)"
        }
    }

    init {
        gridBounds = lines.indices to (0 until lines[0].length)
        processAntennaPoints()
        println(harmonyPoints)
        println(harmonyPoints.size)
    }

    fun processAntennaPoints() {
        antennaPoints.forEach { antennaPoint ->
            val similarAntennas = antennaPoints.filter { it != antennaPoint && it.name == antennaPoint.name && !it.isProcessed}
            calculateAntinodePoints(antennaPoint, similarAntennas)
            antennaPoint.isProcessed = true
        }
    }

    fun calculateAntinodePoints(antennaPoint: AntennaPoint, similarAntennas: List<AntennaPoint>) {
        similarAntennas.forEach { similarAntenna ->
            antinodePoints.addAll(antennaPoint.antinodePoints(similarAntenna))
            harmonyPoints.addAll(antennaPoint.harmonyPoints(similarAntenna))
        } }
}