package ru.dmitrochenko.horsePuzzle.model

import androidx.lifecycle.ViewModel
import ru.dmitrochenko.horsePuzzle.puzzle.HorsePuzzle

class CheckBoardModel : ViewModel() {
    var rows = 3
    var cols = 4
    var hints = 3
    var finishOnStart = false

    var board = 0L
    var currentIndex = -1
    var moves = mutableListOf<Int>()

    private lateinit var availPosition: ByteArray
    private lateinit var puzzle: HorsePuzzle

    fun applySettings(settings: BoardSettingsData) {
        rows = settings.rows
        cols = settings.cols
        hints = settings.hints
        finishOnStart = settings.finishOnStart
    }

    fun isFieldAvailable(id: Int): Boolean {
        return id.toByte() in availPosition
    }

    private fun getAvailablePositions() {
        availPosition = puzzle.getAvailablePosition(board, moves[currentIndex])
    }

    fun setStartPosition(position: Int) {
        puzzle = HorsePuzzle(rows, cols, position)
        makeMove(position)
    }

    fun makeMove(position: Int) {
        board = puzzle.setBit(board, position.toByte())
        if (!stayOnLast()) {
            moves = moves.subList(0, currentIndex + 1)
        }
        moves.add(position)
        currentIndex = moves.size - 1
        getAvailablePositions()
    }

    fun moveBack() {
        if (currentIndex > 0) {
            board = puzzle.unSetBit(board, moves[currentIndex])
            currentIndex--
            getAvailablePositions()
        }
    }

    fun moveForward() {
        if (!stayOnLast()) {
            currentIndex++
            board = puzzle.setBit(board, moves[currentIndex].toByte())
            getAvailablePositions()
        }
    }

    fun stayOnLast():Boolean {
       return currentIndex == moves.size - 1
    }

    fun getCurrentPosition(): Int {
        return moves[currentIndex]
    }

    fun getFieldNameById(id: Int): String {
        val row = id / cols
        val col = id - row * cols
        return getColumnLetter(col) + (row + 1).toString()
    }

    companion object {
        fun getFieldText(row: Int, col: Int): String {
            if (row == 0 && col == 0) {
                return getColumnLetter(col) + (row + 1).toString()
            }
            if (row == 0) {
                return getColumnLetter(col)
            }
            if (col == 0) {
                return (row + 1).toString()
            }
            return ""
        }

        fun getColumnLetter(col: Int): String {
            return ('A' + col).toString()
        }

        fun isWhite(row: Int, col: Int): Boolean {
            return (isColumnIndexEven(col) && !isRowIndexEven(row))
                    || (!isColumnIndexEven(col) && isRowIndexEven(row))
        }

        private fun isColumnIndexEven(col: Int): Boolean {
            return col % 2 == 0
        }

        private fun isRowIndexEven(row: Int): Boolean {
            return row % 2 == 0
        }
    }
}