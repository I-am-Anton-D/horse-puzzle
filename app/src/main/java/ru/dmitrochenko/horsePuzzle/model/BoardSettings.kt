package ru.dmitrochenko.horsePuzzle.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BoardSettingsData(
    var rows: Int = 3,
    var cols: Int = 4,
    var hints: Int = 3,
    var finishOnStart: Boolean = true
) : Parcelable