package ru.dmitrochenko.horsePuzzle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardSettingsData(
    var rows: Int = 3,
    var cols: Int = 4,
    var hints: Int = 3,
    var finishOnStart: Boolean = false
) : Parcelable {

    fun selfCheck(): Boolean {
        return rows * cols >= 12
    }
}