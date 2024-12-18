/*
 * Copyright (c) Keylane B.V. 2001 - 2024
 */

package advent

class Day9(lines: List<String>) {
    private val fileBlockList = mutableListOf<FileBlock>()
    private val wholeFileBlockList = mutableListOf<FileBlock>()

    class FileBlock(val id: Int, val numberOfFiles: Int, val freeSpace: Int?,
                    val originalFiles: MutableList<Int> =  List(numberOfFiles) { id }.toMutableList(),
                    val replacedFiles: MutableList<Int> = mutableListOf()) {

        fun combineFiles(): List<Int> {
            return originalFiles + replacedFiles
        }

        fun computeEmptySpace(): List<String> {
            val movedFiles = numberOfFiles - originalFiles.size
            val freeSpace = freeSpace?.minus(replacedFiles.size) ?: 0
            val freeSpaceAsString = ".".repeat(freeSpace + movedFiles)
            return freeSpaceAsString.toCharArray().map { it.toString() }
        }

        fun repeatDot(times: Int): List<String> {
            return ".".repeat(times).toCharArray().map { it.toString() }
        }

        fun combineWholeFiles(): List<String> {
            val stringFiles = originalFiles.map { it.toString() }
            val replacedStrings = replacedFiles.map { it.toString() }
            val freeSpaceLeft = freeSpace?.minus(replacedFiles.size) ?: 0

            if (originalFiles.isEmpty() && replacedFiles.isNotEmpty()) {
                return repeatDot(numberOfFiles) + replacedStrings + repeatDot(freeSpaceLeft)
            }

            return stringFiles + replacedStrings + computeEmptySpace()
        }

        override fun toString(): String {
            return "FileBlock(id=$id, numberOfFiles=$numberOfFiles, freeSpace=$freeSpace), originalFiles=$originalFiles, replacedFiles=$replacedFiles"
        }
    }

    init {
        for (line in lines) {
            var id = 0
            for (i in line.indices step 2) {
                val freeSpace = if (i + 1 < line.length) line[i + 1].toString().toInt() else null
                fileBlockList.add(FileBlock(id, line[i].toString().toInt(), freeSpace))
                wholeFileBlockList.add(FileBlock(id, line[i].toString().toInt(), freeSpace))
                id++
            }
        }

        processFileBlockList(fileBlockList)
        processWholeFileBlockList(wholeFileBlockList)
    }

    fun processFileBlockList(fileBlockList: List<FileBlock>) {
        val fileOrderList = fileBlockList.toMutableList()
        fileOrderList.reverse()
        fillDiskSpace(fileBlockList, fileOrderList, true)

        val compactDiskChecksum = fileBlockList.map { it.combineFiles() }
            .filter { it.isNotEmpty() }
            .reduce { acc, list -> acc + list }
            .mapIndexed { index, i -> index.toLong() * i }
            .sum()

        println(compactDiskChecksum)
    }

    fun processWholeFileBlockList(fileBlockList: List<FileBlock>) {
        val fileOrderList = fileBlockList.toMutableList()
        fileOrderList.reverse()
        fillDiskSpace(fileBlockList, fileOrderList, false)

        val compactWholeFiles = fileBlockList.map { it.combineWholeFiles() }
            .reduce { acc, list -> acc + list }
            .mapIndexed { index, i ->
                if (i == ".") 0L else index.toLong() * i.toInt()
            }
            .sum()

        println(compactWholeFiles)
    }

    fun fillDiskSpace(fileBlockList: List<FileBlock>, reversedFileList: MutableList<FileBlock>, isFirstPuzzle: Boolean) {
        var currentIndex = 0
        var lastFileBlock = reversedFileList[currentIndex]

        if (isFirstPuzzle) {
            for (fileBlock in fileBlockList) {
                if (fileBlock.id == lastFileBlock.id) {
                    break
                }

                if (fileBlock.freeSpace != null) {
                    while (fileBlock.freeSpace != fileBlock.replacedFiles.size) {
                        if (lastFileBlock.originalFiles.isNotEmpty()) {
                            val newFile = lastFileBlock.originalFiles.removeAt(0)
                            fileBlock.replacedFiles.add(newFile)
                        } else if (fileBlock == reversedFileList[currentIndex + 1]) {
                            break
                        } else {
                            lastFileBlock = reversedFileList[++currentIndex]
                        }
                    }
                }
            }
        } else {
            while (true) {
                for (fileBlock in fileBlockList) {
                    if (fileBlock.id == lastFileBlock.id) {
                        lastFileBlock = reversedFileList[++currentIndex]
                        break
                    }

                    if (fileBlock.freeSpace != null) {
                        if (fileBlock.freeSpace - fileBlock.replacedFiles.size >= lastFileBlock.originalFiles.size) {
                            fileBlock.replacedFiles.addAll(lastFileBlock.originalFiles)
                            lastFileBlock.originalFiles.clear()
                        }
                    }
                }

                if (currentIndex == reversedFileList.size - 1) {
                    break
                }
            }
        }
    }
}