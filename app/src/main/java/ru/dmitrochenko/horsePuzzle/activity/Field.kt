package ru.dmitrochenko.horsePuzzle.activity

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat


class Field @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatButton(context, attrs, defStyle) {

    var horseDrw = 0;
    var markColor = 0
    var white = false
    var fieldColor = 0
        set(value) {
            field = value
            this.setBackgroundColor(value)
        }
    var fieldTextColor = 0
        set(value) {
            field = value
            this.setTextColor(value)
        }

    fun mark() {
        setBackgroundColor(markColor)
    }

    fun umMark() {
        setBackgroundColor(fieldColor)
    }

    fun setHorse() {
        setBackgroundResource(horseDrw)
        background.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(fieldColor,
                BlendModeCompat.DST_OVER)
    }

    fun unSetHorse() {
        setBackgroundResource(0)
        setBackgroundColor(fieldColor)
    }

}