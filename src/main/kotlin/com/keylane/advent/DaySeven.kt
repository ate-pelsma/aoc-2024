/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent

class DaySeven(lines: List<String>) {
    private val lines = lines

    init {
        process()
    }

    class PossibleOutcomes(x: Long, y: Long) {
        val add = x + y
        val multiply = x * y
        val concatanate = (x.toString() + y.toString()).toLong()

        val outcomeList = listOf(add, multiply, concatanate)
        fun contains(solution: Long): Boolean {
            return outcomeList.contains(solution)
        }
    }

    fun process() {
        val totalSum = lines.map { line ->
            val solution = line.substring(0, line.indexOf(":")).toLong()
            val numbers = line.substring(line.indexOf(":") + 2).split(" ").map { it.toLong() }
            solution to numbers
        }. filter { (solution, numbers) ->
            solve(numbers, solution)
        }.sumOf { (solution, _) ->
            solution
        }

        println("Total sum of solutions: $totalSum")
    }

    fun solve(numbers: List<Long>, solution: Long): Boolean {
        val possibleOutcomes = PossibleOutcomes(numbers[0], numbers[1])

        if (numbers.size == 2) {
            return possibleOutcomes.contains(solution)
        }

        val subListAdd = numbers.subList(2, numbers.size).toMutableList().apply { add(0, possibleOutcomes.add) }
        val subListMultiply = numbers.subList(2, numbers.size).toMutableList().apply { add(0, possibleOutcomes.multiply) }
        val subListConcatanate = numbers.subList(2, numbers.size).toMutableList().apply { add(0, possibleOutcomes.concatanate) }

        return solve(subListAdd, solution) || solve(subListMultiply, solution) || solve(subListConcatanate, solution)
    }

}