class Day13(lines: List<String>) {
    val clawMachines = lines.joinToString("\n" ).split("\n\n")
    val regex = Regex("""\d+""")

    init {
        val list = clawMachines.map { regex.findAll(it).map { it.value.toLong() }.toList() }
        val solution = list.sumOf { numbers ->
            val x1 = numbers[0]
            val y1 = numbers[1]
            val x2 = numbers[2]
            val y2 = numbers[3]
            val total1 = numbers[4]
            val total2 = numbers[5]
            solveWithCramer(x1, y1, x2, y2, total1, total2)
        }

        println(solution)
    }

    fun solve(x1: Long, y1: Long, x2: Long, y2: Long, total1: Long, total2: Long): Long {
        if (!isSolvable(x1, x2, total1) || !isSolvable(y1, y2, total2)) {
            return 0
        }

        var count = 1
        var highScore = 0L

        while (count * x1 < total1) {
            val currentX = x1 * count
            val leftOver = total1 - currentX

            if (leftOver % x2 != 0L) {
                count++
                continue
            }

            val buttonB = leftOver / x2
            if (count * y1 + buttonB * y2 == total2) {
                val score = count * 3 + buttonB
                if (score > highScore) {
                    highScore = score
                }
            }

            count++
        }

        return highScore
    }

    fun solveWithCramer(x1: Long, y1: Long, x2: Long, y2: Long, total1: Long, total2: Long): Long {
        val a: Double = (total1.toDouble() * y2 - total2 * x2) / (x1 * y2 - y1 * x2)
        val b: Double = (total2.toDouble() * x1 - total1 * y1) / (x1 * y2 - y1 * x2)

        if (a == a.toLong().toDouble() && b == b.toLong().toDouble()) {
            println("a: $a, b: $b")
            return (3 * a + b).toLong()
        } else {
            println("No solution")
            return 0L
        }
    }

    fun gcd(a: Long, b: Long): Long {
        return if (a % b == 0L) b else gcd(b, a % b)
    }

    fun isSolvable(a: Long, b: Long, c: Long): Boolean {
        val gcd = gcd(a, b)
        return c % gcd == 0L
    }
}