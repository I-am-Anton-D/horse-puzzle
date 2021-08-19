package ru.dmitrochenko.horsePuzzle.model

import androidx.lifecycle.ViewModel

class CheckBoardModel : ViewModel() {
    var rows = 3
    var cols = 4
    var hints = 3
    var finishOnStart = false
    var board = 0L
    var start = -1
    var last = -1
    var current = -1;

    fun applySettings(settings: BoardSettingsData) {
        rows = settings.rows
        cols = settings.cols
        hints = settings.hints
        finishOnStart = settings.finishOnStart
    }

    companion object {
        fun getFieldNameById(id: Int, cols: Int): String {
            val row = id / cols
            val col = id - row * cols
            return getColumnLetter(col) + (row + 1).toString()
        }

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