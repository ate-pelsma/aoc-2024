/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent.old

class DayFour(private val cards: List<String>) {
    private val winningNumbers: List<List<Int>>;
    private val playerNumbers: List<List<Int>>
    private var totalCards = 0

    init {
        val numbers = cards.map { it.split(":")[1] }
        winningNumbers = numbers
            .map { it.split("|")[0] }
            .map { it.split(" ") }
            .map { it.filter { value -> value.isNotEmpty() && value.all { char -> char.isDigit() } } }
            .map { it.map { value -> value.toInt() } }
        playerNumbers = numbers
            .map { it.split("|")[1] }
            .map { it.split(" ") }
            .map { it.filter { value -> value.isNotEmpty() && value.all { char -> char.isDigit() } } }
            .map { it.map { value -> value.toInt() } }
    }

    fun calculatePoints(): Int {
        playGame(0, winningNumbers.size)
        return totalCards
    }

    fun playGame(start: Int, end: Int) {
        for (i in start until end) {
            totalCards += 1

            // Amount of overlap
            val overlappingNumbers = winningNumbers[i].toSet().intersect(playerNumbers[i].toSet()).size

            if (overlappingNumbers > 0) {
//                println("Playing MINI game ${i + 1}: there were $overlappingNumbers overlapping numbers")
                playGame(i + 1, i + 1 + overlappingNumbers)
            } else {
//                println("Playing MINI game ${i + 1}: there were no overlapping numbers")
            }
        }
    }
}