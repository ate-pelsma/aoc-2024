import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class DayEleven(input: List<String>) {
    var stoneMap: ConcurrentMap<Long, Long> = ConcurrentHashMap()

    init {
        input[0].split(" ").map { it.toLong() }.forEach { value ->
            stoneMap[value] = 1
        }

        keepBlinking(75)
        println("Total stone entries: ${stoneMap.size}")
        println("Total stones: ${stoneMap.values.sum()}")
    }

    private fun keepBlinking(blinks: Int = 75) {
        for (i in 1 .. blinks) {
            blink()
//            println("Blink $i: ${stoneMap}")
        }
    }

    fun blink() {
        val currentEntries = stoneMap.entries.toList()
        for (stoneEntry in currentEntries) {
            val (stone, count) = stoneEntry
            if (count == 0L) {
                continue
            }

            if (stone == 0L) {
                stoneMap[1] = stoneMap.getOrDefault(1, 0L) + count
                stoneMap[0] = stoneMap[0]!! - count
            } else if (stone.toString().length % 2 == 0) {
                val stoneString = stone.toString()
                val half = stoneString.length / 2
                val left = stoneString.substring(0, half).toLong()
                val right = stoneString.substring(half).toLong()
                stoneMap[left] = stoneMap.getOrDefault(left, 0L) + count
                stoneMap[right] = stoneMap.getOrDefault(right, 0L) + count
                stoneMap[stone] = stoneMap[stone]!! - count
            } else {
                stoneMap[stone * 2024] = stoneMap.getOrDefault(stone * 2024, 0L) + count
                stoneMap[stone] = stoneMap[stone]!! - count
            }
        }
    }
}