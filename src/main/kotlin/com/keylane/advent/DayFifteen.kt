/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent

class DayFifteen(lines: List<String>) {

    companion object {
        lateinit var warehouseState: MutableList<WarehouseObject>
        lateinit var secondPuzzleState: MutableList<WarehouseObject>
        lateinit var relevantState: MutableList<WarehouseObject>
    }

    val moveSet = lines.subList(lines.indexOf("") + 1, lines.size).flatMap{ it.toList() }

    open class WarehouseObject(var position: Pair<Int, IntRange>, val type: String) {
        open val surroundingObjects = mutableListOf<WarehouseObject>()

        fun loadSurroundingObjects() {
            if (this is WarehouseWall) {
                return
            }

            listOf(
                position.first - 1 to position.second,
                position.first + 1 to position.second,
                position.first to position.second.first - 1 .. position.second.first - 1,
                position.first to position.second.last + 1 .. position.second.last + 1
            ).forEach { (x, y) ->
                surroundingObjects.addAll(relevantState.filter { it.position.first == x && y.intersect(it.position.second).isNotEmpty() }.toList())
            }
        }

        fun reloadSurroundingObjects() {
            surroundingObjects.clear()
            loadSurroundingObjects()
        }

        fun getLeft(): List<WarehouseObject> {
            val tempList: MutableList<WarehouseObject> = mutableListOf()
            tempList.addAll(surroundingObjects.filter { it.position.first == position.first && it.position.second.last == position.second.first - 1 }.toList())

            return tempList
        }

        fun getRight(): List<WarehouseObject> {
            val tempList: MutableList<WarehouseObject> = mutableListOf()
            tempList.addAll(surroundingObjects.filter { it.position.first == position.first && it.position.second.first == position.second.last + 1 }.toList())

            return tempList
        }

        fun getAbove(): List<WarehouseObject> {
            val tempList: MutableList<WarehouseObject> = mutableListOf()
            tempList.addAll(surroundingObjects.filter { (it.position.first == (position.first - 1)) && (it.position.second.intersect(position.second).isNotEmpty()) }.toList())

            return tempList
        }

        fun getBelow(): List<WarehouseObject> {
            val tempList: MutableList<WarehouseObject> = mutableListOf()
            tempList.addAll(surroundingObjects.filter { (it.position.first == (position.first + 1)) && (it.position.second.intersect(position.second).isNotEmpty()) }.toList())

            return tempList
        }

        override fun toString(): String {
            val surroundingObjectsStr = surroundingObjects.map { "(${it.position.first}, ${it.position.second}, ${it.type})" }
            return "WarehouseObject(position=$position, type=$type, surroundingObjects=$surroundingObjectsStr)"
        }
    }

    class WarehouseWall(position: Pair<Int, IntRange>) : WarehouseObject(position, "#") {
    }

    class WarehouseBox(position: Pair<Int, IntRange>) : WarehouseObject(position, "O") {
    }

    class WarehouseDoubleBox(position: Pair<Int, IntRange>) : WarehouseObject(position, "[]") {
    }

    class WarehouseFreespace(position: Pair<Int, IntRange>) : WarehouseObject(position, ".") {
    }

    class WarehouseRobot(position: Pair<Int, IntRange>) : WarehouseObject(position, "@") {
    }


    init {
        warehouseState = lines.subList(0, lines.indexOf("")).flatMapIndexed{ rowIndex, line ->
            line.mapIndexed { colIndex, char ->
                when(char) {
                    '#' -> WarehouseWall(rowIndex to colIndex .. colIndex)
                    'O' -> WarehouseBox(rowIndex to colIndex .. colIndex)
                    '@' -> WarehouseRobot(rowIndex to colIndex .. colIndex)
                    else -> WarehouseFreespace(rowIndex to colIndex .. colIndex)
                }
            }
        }.toMutableList()

        secondPuzzleState = lines.subList(0, lines.indexOf("")).flatMapIndexed{ rowIndex, line ->
            line.flatMapIndexed { colIndex, char ->
                val colIndexDoubled = colIndex * 2
                val doubleIndexOffset = colIndexDoubled + 1
                when (char) {
                    '#' -> listOf(WarehouseWall(rowIndex to colIndexDoubled .. colIndexDoubled), WarehouseWall(rowIndex to doubleIndexOffset .. doubleIndexOffset))
                    'O' -> listOf(WarehouseDoubleBox(rowIndex to colIndexDoubled .. doubleIndexOffset))
                    '@' -> listOf(WarehouseRobot(rowIndex to colIndexDoubled .. colIndexDoubled), WarehouseFreespace(rowIndex to doubleIndexOffset .. doubleIndexOffset))
                    else -> listOf(WarehouseFreespace(rowIndex to colIndexDoubled .. colIndexDoubled), WarehouseFreespace(rowIndex to doubleIndexOffset .. doubleIndexOffset))
                }
            }
        }.toMutableList()

        relevantState = secondPuzzleState
//        warehouseState.forEach { it.loadSurroundingObjects() }
        secondPuzzleState.forEach { it.loadSurroundingObjects() }

        move(secondPuzzleState)
    }

    fun move(state: List<WarehouseObject>) {
        for (move in moveSet.indices) {
            val currentPositionRobot = state.find { it is WarehouseRobot }!!
            when (moveSet[move]) {
                '<' -> makeMove(WarehouseObject::getLeft, listOf(currentPositionRobot), mutableSetOf(currentPositionRobot), "left")
                '>' -> makeMove(WarehouseObject::getRight, listOf(currentPositionRobot), mutableSetOf(currentPositionRobot), "right")
                '^' -> makeMove(WarehouseObject::getAbove, listOf(currentPositionRobot), mutableSetOf(currentPositionRobot), "up")
                'v' -> makeMove(WarehouseObject::getBelow, listOf(currentPositionRobot), mutableSetOf(currentPositionRobot), "down")
            }

            if (move % 1000 == 0) {
                println()
                println("Just performed move number $move: ${moveSet[move]} leading to ....")
                printEndState(state)
            }
        }

        printEndState(state)
        state.filter { it is WarehouseDoubleBox }.sumOf { 100 * it.position.first + it.position.second.first }.also { println(it)
        }
    }

    fun makeMove(direction: (WarehouseObject) -> List<WarehouseObject>, currentObject: List<WarehouseObject>, affectedObjects: MutableSet<WarehouseObject>, directionStr: String) {
        val nextObjects = currentObject.flatMap { direction(it) }.toList()
        if (nextObjects.any { it is WarehouseWall }) {
            return
        }

        affectedObjects.addAll(nextObjects)

        if (nextObjects.isNotEmpty() && nextObjects.all { it is WarehouseFreespace }) {
            swapPosition(affectedObjects, directionStr)
            return
        }

        if (nextObjects.isNotEmpty() && nextObjects.all { it is WarehouseDoubleBox || it is WarehouseBox || it is WarehouseFreespace }) {
            makeMove(direction, nextObjects.filter{ it is WarehouseDoubleBox || it is WarehouseBox }.toList(), affectedObjects, directionStr)
        }
    }

    fun swapPosition(affectedObjects: Set<WarehouseObject>, directionStr: String) {

        if (directionStr == "left" || directionStr == "right") {
            swapHorizontal(affectedObjects, directionStr)
        } else {
            // Create a list of freespace objects
            val freespaceObjects = affectedObjects.filterIsInstance<WarehouseFreespace>().toSet()

            // Create variable that holds the positions of the objects before moving
            val oldPositions = affectedObjects.filter { it !is WarehouseFreespace }.map { it.position }

            // Actually change the positions of the objects
            alterPositions(affectedObjects, directionStr)

            // Create variable that holds the positions of the objects after moving
            val newPositions = affectedObjects.filter { it !is WarehouseFreespace }.map { it.position }

            // Create a list of positions that are not in the new positions list and split them
            val oldPositionsNowFree = splitPositions(oldPositions.toSet()).subtract(splitPositions(newPositions.toSet()).toSet())

            if (freespaceObjects.size != oldPositionsNowFree.size) {
                println("Something went wrong")
            } else {
                freespaceObjects.zip(oldPositionsNowFree).forEach { (freespace, position) -> freespace.position = position }
            }
        }

        val affectedRows = affectedObjects.minOf { it.position.first } - 2..affectedObjects.maxOf { it.position.first } + 2
        val affectedCols = affectedObjects.minOf { it.position.second.first } - 2..affectedObjects.maxOf { it.position.second.last } + 2
        relevantState.filter { it.position.first in affectedRows && it.position.second.intersect(affectedCols).isNotEmpty() }.forEach { it.reloadSurroundingObjects() }
    }

    fun swapHorizontal(affectedObjects: Set<WarehouseObject>, directionStr: String) {
        val previousRobotPosition = affectedObjects.find { it is WarehouseRobot }!!.position

        // Actually change the positions of the objects
        alterPositions(affectedObjects, directionStr)

        affectedObjects.find { it is WarehouseFreespace }!!.position = previousRobotPosition
    }

    fun alterPositions(affectedObjects: Set<WarehouseObject>, directionStr: String) {
        affectedObjects.filter { it !is WarehouseFreespace }.forEach {
            it.position = when (directionStr) {
                "left" -> it.position.first to it.position.second.first - 1.. it.position.second.last - 1
                "right" -> it.position.first to it.position.second.first + 1 .. it.position.second.last + 1
                "up" -> it.position.first - 1 to it.position.second
                "down" -> it.position.first + 1 to it.position.second
                else -> return
            }
        }
    }

    fun splitPositions(positions: Set<Pair<Int, IntRange>>): List<Pair<Int, IntRange>> {
        return positions.flatMap { (x, range) ->
            range.map { x to it..it }
        }
    }

    fun printEndState(state: List<WarehouseObject>) {
        // Initialize the grid
        val grid = Array(state.maxOf { it.position.first } + 1) { CharArray(state.maxOf { it.position.second.first } + 1) { '.' } }

        // Mark the positions of the objects
        state.forEach { obj ->
            for (i in obj.position.second) {
                if (obj.type.length > 1) {
                    grid[obj.position.first][i] = obj.type[i - obj.position.second.first]
                } else {
                    grid[obj.position.first][i] = obj.type[0]
                }
            }
        }

        // Print the grid
        for (row in grid) {
            println(row.joinToString(""))
        }
    }
}