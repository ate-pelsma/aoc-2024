/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent

import org.springframework.util.StringUtils

class DayEleven(lines: List<String>) {
    private val stones = lines[0].split(" ")
    var timesBlinked: Int = 0

    init {

    }

    fun blink(stones: Sequence<String>, timesBlinked: Int) {
        if (timesBlinked == 6) {
            println(stones.count())
            return
        }

        val newStones = mutableListOf<String>()

        for (stone in stones) {
            if (stone == "0") {
                newStones.add("1")
                continue
            }

            if (stone.length % 2 == 0) {
                newStones.addAll(splitStone(stone))
                continue
            }

            newStones.add((stone.toLong() * 2024).toString())
        }

        blink(newStones.asSequence(), timesBlinked + 1)
    }

    fun checkStone(stone: String): List<String> {
        if (stone == "0") {
            return listOf("1")
        }

        if (stone.length % 2 == 0) {
            return splitStone(stone)
        }

        return listOf((stone.toLong() * 2024).toString())
    }

    fun splitStone(stone: String): List<String> {
        val half = stone.length / 2
        return listOf(stone.substring(0, half), stone.substring(half).trimStart{ it == '0' }.ifBlank { "0" } )
    }
}