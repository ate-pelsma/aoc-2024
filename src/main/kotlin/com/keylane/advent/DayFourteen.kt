/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent

class DayFourteen(lines: List<String>) {
    val robotList = lines.map { line ->
        val startingIndex = line.substring(line.indexOf("p=") + 2, line.indexOf(" ")).split(",").map { it.toInt() }.toMutableList()
        val moveSet = line.substring(line.indexOf("v=") + 2).split(",").map { it.toInt() }.toList()
        (startingIndex[0] to startingIndex[1]) to (moveSet[0] to moveSet[1])
    }
    val newPositions: MutableList<Pair<Int, Int>> = mutableListOf()
    val quadList = mutableListOf(mutableListOf(0 ,0), mutableListOf(0,0))

    init {
        robotList.forEach { robot ->
            moveRobots(robot)
        }
        println(quadList.flatten().reduce { acc, i -> acc * i })

        checkForChristmasTree()
    }

    fun moveRobots(robot: Pair<Pair<Int, Int>, Pair<Int, Int>>, times: Int = 100, width: Int = 101, height: Int = 103) {

        val (start, move) = robot
        val (x, y) = start
        val (dx, dy) = move
        val modX = ((x + times * dx) % width + width) % width // Java/Kotlin % operator is remainder not modulus... that one hurt to find out
        val modY = ((y + times * dy) % height + height) % height

        if (modX == (width - 1) / 2 || modY == (height - 1) / 2) {
            return
        }

        val quadX = if (modX < (width - 1) / 2) 0 else 1
        val quadY = if (modY < (height - 1) / 2) 0 else 1

        quadList[quadX][quadY]++
        newPositions.add(modX to modY)
    }

    fun checkForChristmasTree() {
        var times = 1
        while (times <= 10000) {
            newPositions.clear()
            robotList.forEach { robot ->
                moveRobots(robot, times)
            }

            if (isPossibleTree()) {
                println("Found possible tree at: $times")
                printGrid()
            }

            times++
        }
    }

    /**
     * The idea here is that if there are more than 200 robots close to each other, it is likely that they are forming a tree.
     * The number 200 was found by trial and error. It is not a perfect solution, but it works for the input.
     */
    fun isPossibleTree(): Boolean {
        val closeRobots: MutableSet<Pair<Int, Int>> = mutableSetOf()
        newPositions.forEach { (x, y) ->
            val xRange = (x - 1)..(x + 1)
            val yRange = (y - 1)..(y + 1)
            newPositions.filter { (nx, ny) -> (nx != x && ny != y) && (nx in xRange && ny in yRange) }.forEach { closeRobots.add(it) }
        }

        return closeRobots.size > 200
    }

    fun printGrid() {
        // Initialize the grid
        val grid = Array(103) { CharArray(101) { '.' } }

        // Mark the positions of the robots
        newPositions.forEach { (x, y) ->
            grid[y][x] = 'X'
        }

        println("Grid representation at")

        // Print the grid
        for (row in grid) {
            println(row.joinToString(""))
        }

        println()
    }
}