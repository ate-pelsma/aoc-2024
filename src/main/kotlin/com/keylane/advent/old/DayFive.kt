/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package com.keylane.advent.old

import kotlinx.coroutines.*

class DayFive(input: List<String>) {
    private val seeds: List<Long>
    private val rangedSeeds: List<Pair<Long, Long>>
    private val toSoil: Map<Pair<Long, Long>, Long>
    private val toFertilizer: Map<Pair<Long, Long>, Long>
    private val toWater: Map<Pair<Long, Long>, Long>
    private val toLight: Map<Pair<Long, Long>, Long>
    private val toTemperature: Map<Pair<Long, Long>, Long>
    private val toHumidity: Map<Pair<Long, Long>, Long>
    private val toLocation: Map<Pair<Long, Long>, Long>

    private val everyMap: List<Map<Pair<Long, Long>, Long>>

    init {
        val sections = mutableMapOf<String, MutableList<String>>()
        var currentSection = ""

        for (line in input) {
            if (line.contains("map:")) {
                currentSection = line.split(":")[0].split(" ")[0].trim()
                sections[currentSection] = mutableListOf()
            } else if (line.isNotBlank()) {
                sections[currentSection]?.add(line)
            }
        }

        seeds = input[0].split(":")[1].trim().split(" ").map { it.toLong() }
        rangedSeeds = parseSeedRanges(seeds)
        toSoil = parseMatrix(sections["seed-to-soil"] ?: emptyList())
        toFertilizer = parseMatrix(sections["soil-to-fertilizer"] ?: emptyList())
        toWater = parseMatrix(sections["fertilizer-to-water"] ?: emptyList())
        toLight = parseMatrix(sections["water-to-light"] ?: emptyList())
        toTemperature = parseMatrix(sections["light-to-temperature"] ?: emptyList())
        toHumidity = parseMatrix(sections["temperature-to-humidity"] ?: emptyList())
        toLocation = parseMatrix(sections["humidity-to-location"] ?: emptyList())

        everyMap = listOf(toSoil, toFertilizer, toWater, toLight, toTemperature, toHumidity, toLocation)
    }

    private fun parseSeedRanges(seeds: List<Long>): List<Pair<Long, Long>> {
        val seedRanges = mutableListOf<Pair<Long, Long>>()
        for (i in seeds.indices step 2) {
            val start = seeds[i]
            val end = start + seeds[i + 1]
            seedRanges.add(Pair(start, end))
        }

        return seedRanges
    }

    private fun parseMatrix(lines: List<String>): Map<Pair<Long, Long>, Long> {
        val numberMap: MutableMap<Pair<Long, Long>, Long> = mutableMapOf()
        val numberList = lines.map { line -> line.trim().split(" ").map { it.toLong() } }

        for (conversionList in numberList) {
            val sourceNumber: Long = conversionList[1]
            val changeFactor: Long = conversionList[0] - sourceNumber
            val rangeIndicator: Pair<Long, Long> = Pair(sourceNumber, sourceNumber + conversionList[2])

            numberMap[rangeIndicator] = changeFactor
        }

        return numberMap
    }

    fun runThroughAlmanac() = runBlocking {
        val locationNumber: Long
        val deferredResults = rangedSeeds.mapIndexed() { index, (start, end) ->
            async {
                var localMin = Long.MAX_VALUE

                for (seed in start until end) {
                    var localNumber = seed

                    for (map in everyMap) {
                        localNumber = convertNumber(map, localNumber)
                    }

                    if (localNumber < localMin) {
                        localMin = localNumber
                    }
                }

                localMin
            }
        }
        locationNumber = deferredResults.awaitAll().minOrNull() ?: Long.MAX_VALUE
        println("The lowest location number is: $locationNumber")
    }

    private fun preprocessMap(map: Map<Pair<Long, Long>, Long>): List<Triple<Long, Long, Long>> {
        return map.map { (range, change) -> Triple(range.first, range.second, change) }
            .sortedBy { it.first }
    }

    private fun convertNumber(numberMap: Map<Pair<Long, Long>, Long>, currentNumber: Long): Long {
        for ((rangePair, changeFactor) in numberMap) {
            if (currentNumber >= rangePair.first && currentNumber < rangePair.second) {
                return currentNumber + changeFactor
            }
        }

        return currentNumber
    }

    private fun convertNumberOptimized(preprocessedMap: List<Triple<Long, Long, Long>>, currentNumber: Long): Long {
        val index = preprocessedMap.binarySearch { ( start, end, _) ->
            when {
                currentNumber < start -> 1
                currentNumber >= end -> -1
                else -> 0
            }
        }

        return if (index >= 0) currentNumber + preprocessedMap[index].third else currentNumber
    }

    private fun processRange(start: Long, end: Long, maps: List<List<Triple<Long, Long, Long>>>): Long {
        var minLocation = Long.MAX_VALUE

        for (seed in start until end) {
            var currentNumber = seed
            for (map in maps) {
                currentNumber = convertNumberOptimized(map, currentNumber)
            }
            minLocation = minOf(minLocation, currentNumber)
        }
        return minLocation
    }

    fun runThroughAlmanacOptimized() = runBlocking {
        val preprocessedMaps = everyMap.map { preprocessMap(it) }
        val locationNumber = rangedSeeds.map { (start, end) ->
            async { processRange(start, end, preprocessedMaps) }
        }.awaitAll().minOrNull() ?: Long.MAX_VALUE

        println("The lowest location number is: $locationNumber")
    }
}