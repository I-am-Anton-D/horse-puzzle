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

    private var hintPositions = mutableListOf<Byte>()
    private var hintsPaths = mutableListOf<ByteArray>()

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

    fun getAvailablePositions(): ByteArray {
        return availPosition
    }

    fun initAvailablePositions() {
        availPosition = puzzle.getAvailablePosition(board, getCurrentPosition())
    }

    fun setStartPosition(position: Int) {
        puzzle = HorsePuzzle(rows, cols, position)
        makeMove(position)
    }

    fun getCurrentPosition(): Int {
        return moves[currentIndex]
    }

    fun makeMove(position: Int) {
        board = puzzle.setBit(board, position.toByte())
        if (!stayOnLast()) {
            moves = moves.subList(0, currentIndex + 1)
        }

        if (position.toByte() in hintPositions && getCountOfRemainingMoves() != 1 && !noHints) {
            proceedHintsPath(position)
        }

        moves.add(position)
        currentIndex = moves.size - 1
        initAvailablePositions()
    }

    private fun proceedHintsPath(position: Int) {
        hintsPaths = hintsPaths
            .filter { p -> puzzle.getMovePosition(p[0], getCurrentPosition()) == position.toByte() }
            .map { p -> p.copyOfRange(1, p.size) }
            .toMutableList()
        hintPositions = hintsPaths.map { p -> puzzle.getMovePosition(p[0], position) }.distinct().toMutableList()
    }

    fun moveBack() {
        if (currentIndex > 0) {
            board = puzzle.unSetBit(board, getCurrentPosition())
            currentIndex--
            hintPositions.clear()
            hintsPaths.clear()
            initAvailablePositions()
        }
    }

    fun moveForward() {
        if (!stayOnLast()) {
            currentIndex++
            board = puzzle.setBit(board,  getCurrentPosition().toByte())
            hintPositions.clear()
            hintsPaths.clear()
            initAvailablePositions()
        }
    }

    fun stayOnLast(): Boolean {
        return currentIndex == moves.size - 1
    }

    fun getFieldNameById(id: Int): String {
        val row = id / cols
        val col = id - row * cols
        return getColumnLetter(col) + (row + 1).toString()
    }

    fun getCountOfRemainingMoves(): Int {
        return rows * cols - moves.subList(0, currentIndex).size - 1
    }

    fun checkBoard(): Boolean {
        return puzzle.checkBoard(board)
    }

    fun getHintsPositions(): MutableList<Byte> {
        return hintPositions
    }

    fun cancelCalculate() {
        puzzle.cancelCalculate();
    }

    fun getCombinations(): Long {
        val limit = if (getCountOfRemainingMoves() < 32) Int.MAX_VALUE else 15000
        val madeMoves = moves.subList(0, currentIndex).size
        val from = getCurrentPosition()
        val count = puzzle.calculateLimitPosition(board, from, madeMoves, limit, finishOnStart)
        hintPositions = puzzle.hintsMoves
        hintsPaths = puzzle.hintsPaths
        return count
    }

    fun canReachStart(): Boolean {
        val availablePosition = puzzle.getAvailablePosition(0L, moves[0])
        if (getCurrentPosition().toByte() in availablePosition) {
            return true;
        }
        return false
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