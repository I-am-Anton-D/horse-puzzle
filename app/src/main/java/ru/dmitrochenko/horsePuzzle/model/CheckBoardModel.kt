package ru.dmitrochenko.horsePuzzle.model

import androidx.lifecycle.ViewModel
import ru.dmitrochenko.horsePuzzle.puzzle.HorsePuzzle

class CheckBoardModel : ViewModel() {
    var rows = 3
    var cols = 4
    var hints = 3
    var finishOnStart = false
    var noHints = false

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
        noHints = hints == 0
    }

    fun isFieldAvailable(id: Int): Boolean {
        return id.toByte() in availPosition
    }

    fun getAvailablePositions():ByteArray {
        return availPosition
    }

    fun initAvailablePositions() {
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
        initAvailablePositions()
    }

    fun moveBack() {
        if (currentIndex > 0) {
            board = puzzle.unSetBit(board, moves[currentIndex])
            currentIndex--
            initAvailablePositions()
        }
    }

    fun moveForward() {
        if (!stayOnLast()) {
            currentIndex++
            board = puzzle.setBit(board, moves[currentIndex].toByte())
            initAvailablePositions()
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

    fun fullCalculate():Long {
        return puzzle.calculatePosition(board, getCurrentPosition().toLong(), moves.subList(0,currentIndex).size)
    }

    fun limitCalculate():Long {
        return puzzle.calculateLimitPosition(board, getCurrentPosition().toLong(), moves.subList(0,currentIndex).size)
    }

    fun getCountOfRemainingMoves():Int {
        return rows * cols - moves.subList(0, currentIndex).size - 1
    }

    fun checkBoard(): Boolean {
        return puzzle.checkBoard(board)
    }

    fun getHint():Byte {
        return puzzle.getHint();
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