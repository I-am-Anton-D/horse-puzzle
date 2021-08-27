package ru.dmitrochenko.horsePuzzle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardSettingsData(
    var rows: Int = 3,
    var cols: Int = 4,
    var hints: Int = 3,
    var finishOnStart: Boolean = false,
    var selfCheck: Boolean = true
) : Parcelable {

    fun selfCheck(): Boolean {
        val notExist = arrayOf(9, 15, 16, 18)
        selfCheck = if (!finishOnStart) {
            rows * cols !in notExist
        } else {
            rows * cols >= 30 && rows * cols != 35
        }
        if (!selfCheck) hints = 0
        return selfCheck;
    }
}