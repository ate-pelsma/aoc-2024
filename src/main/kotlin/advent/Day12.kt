import java.util.SortedSet

class Day12(lines: List<String>) {

    companion object {
        var gardenPlotList: List<GardenPlot> = mutableListOf()
        val regionList: MutableList<GardenRegion> = mutableListOf()
        var maxRow: Int = 0
        var maxCol: Int = 0
        val nonePlot = GardenPlot(-1, -1, "None")
    }

    class GardenPlot(val x: Int, val y: Int, val typeOfPlant: String, val surroundingPlants: MutableList<GardenPlot> = mutableListOf()) {
        var gardenRegion: GardenRegion? = null

        fun fillSurroundingPlants() {
            listOf(
                0 to -1,
                0 to 1,
                -1 to 0,
                1 to 0
            ).forEach { (dx, dy) ->
                gardenPlotList.find { it.x == x + dx && it.y == y + dy }?.let {
                    surroundingPlants.add(it)
                } ?: surroundingPlants.add(getNonePlot())
            }
        }

        fun getPlotOnTheRight(): GardenPlot {
            return surroundingPlants.find { it.y == y + 1 } ?: getNonePlot()
        }

        fun getPlotOnTheLeft(): GardenPlot {
            return surroundingPlants.find { it.y == y - 1 } ?: getNonePlot()
        }

        fun getPlotUnderneath(): GardenPlot {
            return surroundingPlants.find { it.x == x + 1 } ?: getNonePlot()
        }

        fun getPlotAbove(): GardenPlot {
            return surroundingPlants.find { it.x == x - 1 } ?: getNonePlot()
        }

        fun getNonePlot(): GardenPlot {
            return nonePlot
        }

        override fun toString(): String {
            val surroundingPlantsStr = surroundingPlants.joinToString(", ") { "${it.typeOfPlant}: (${it.x}, ${it.y})" }
            return "GardenPlot(x=$x, y=$y, typeOfPlant='$typeOfPlant', surroundingPlants=$surroundingPlantsStr)"
        }
    }

    class GardenRegion(val typeOfPlant: String) {
        val gardenPlots: SortedSet<GardenPlot> = sortedSetOf(compareBy({ it.x }, { it.y }))

        fun populate(plots: List<GardenPlot>) {
            for (gardenPlot in plots) {
                if (gardenPlot.gardenRegion != null) {
                    continue
                }

                addPlot(gardenPlot)
                populate(gardenPlot.surroundingPlants.filter { it.typeOfPlant == gardenPlot.typeOfPlant && it.gardenRegion == null })
            }
        }

        fun addPlot(gardenPlot: GardenPlot) {
            gardenPlots.add(gardenPlot)
            gardenPlot.gardenRegion = this
        }

        fun calculateScore(): Long {
            return calculateArea().toLong() * calculatePerimeter()
        }

        fun calculateArea(): Int {
            return gardenPlots.size
        }

        fun calculatePerimeter(): Int {
            return gardenPlots.flatMap { it.surroundingPlants }.filter { it.typeOfPlant != typeOfPlant }.size
        }

        fun calculateEdges(): Int {
            if (isRectangle()) {
                return 4
            }

            val uniqueHorizontalBottomEdges = mutableSetOf<Pair<IntRange, IntRange>>()
            val uniqueHorizontalUpperEdges = mutableSetOf<Pair<IntRange, IntRange>>()
            val uniqueVerticalLeftEdges = mutableSetOf<Pair<IntRange, IntRange>>()
            val uniqueVerticalRightEdges = mutableSetOf<Pair<IntRange, IntRange>>()

            detectEdges(GardenPlot::getPlotOnTheRight, GardenPlot::getPlotUnderneath, uniqueHorizontalBottomEdges)
            detectEdges(GardenPlot::getPlotOnTheRight, GardenPlot::getPlotAbove, uniqueHorizontalUpperEdges)
            detectEdges(GardenPlot::getPlotUnderneath, GardenPlot::getPlotOnTheLeft, uniqueVerticalLeftEdges)
            detectEdges(GardenPlot::getPlotUnderneath, GardenPlot::getPlotOnTheRight, uniqueVerticalRightEdges)

            println("Total perimeter for region $typeOfPlant: ${uniqueHorizontalBottomEdges.size + uniqueHorizontalUpperEdges.size + uniqueVerticalLeftEdges.size + uniqueVerticalRightEdges.size}")

            return uniqueHorizontalBottomEdges.size + uniqueHorizontalUpperEdges.size + uniqueVerticalLeftEdges.size + uniqueVerticalRightEdges.size
        }

        private fun isRectangle(): Boolean {
            val surfaceArea = ((gardenPlots.maxOf { it.x } - gardenPlots.minOf { it.x } + 1)
                    * (gardenPlots.maxOf { it.y } - gardenPlots.minOf { it.y } + 1))
            if (surfaceArea == gardenPlots.size) {
                // A rectangle always has 4 edges
                println("No special edges detected for region $typeOfPlant so the price is ${surfaceArea * 4}")
                return true
            }

            return false
        }

        private fun detectEdges(getNextPlot: (GardenPlot) -> GardenPlot,
                                getAdjacentPlot: (GardenPlot) -> GardenPlot,
                                uniqueEdges: MutableSet<Pair<IntRange, IntRange>>) {
            for (gardenPlot in gardenPlots) {
                val (x, y) = gardenPlot.x to gardenPlot.y
                if (uniqueEdges.any { x in it.first && y in it.second }) {
                    continue
                }

                var currentPlot = gardenPlot
                if (getAdjacentPlot(gardenPlot).typeOfPlant != typeOfPlant) {
                    val startingPoint = currentPlot.x to y
                    while (getNextPlot(currentPlot).typeOfPlant == gardenPlot.typeOfPlant
                        && getAdjacentPlot(getNextPlot(currentPlot)).typeOfPlant != gardenPlot.typeOfPlant) {
                        currentPlot = getNextPlot(currentPlot)
                    }

                    val endingPoint = currentPlot.x to currentPlot.y
                    uniqueEdges.add(startingPoint.first .. endingPoint.first to startingPoint.second .. endingPoint.second)
                }
            }
        }

        override fun toString(): String {
            val gardenPlotString = gardenPlots.map { String.format("(%s, %s)", it.x, it.y) }.joinToString { it.toString() }
            return "GardenRegion(typeOfPlant='$typeOfPlant', gardenPlots=$gardenPlotString)"
        }
    }

    init {
        gardenPlotList = lines.flatMapIndexed { x: Int, line: String ->
            line.mapIndexed { y, c -> GardenPlot(x, y, c.toString()) }
        }
        maxRow = gardenPlotList.maxOf { it.x }
        maxCol = gardenPlotList.maxOf { it.y }

        gardenPlotList.forEach { it.fillSurroundingPlants() }
        populateRegions()
        calculateScore()

        val edgeScore = regionList.sumOf { it.calculateEdges() * it.calculateArea() }
        println("Total edge score: $edgeScore")
    }

    fun calculateScore() {
        val scores = regionList.map { it.typeOfPlant to it.calculateScore() }
        println("Scores: $scores")
        println("Total score: ${scores.sumOf { it.second }}")
    }

    fun populateRegions() {
        for (gardenPlot in gardenPlotList) {

            if (gardenPlot.gardenRegion == null) {
                val gardenRegion = GardenRegion(gardenPlot.typeOfPlant)
                gardenRegion.addPlot(gardenPlot)
                gardenRegion.populate(gardenPlot.surroundingPlants.filter { it.typeOfPlant == gardenPlot.typeOfPlant && it.gardenRegion == null })
                regionList.add(gardenRegion)
            }
        }
    }
}