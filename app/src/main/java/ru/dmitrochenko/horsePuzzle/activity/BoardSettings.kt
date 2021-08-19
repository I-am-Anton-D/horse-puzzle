package ru.dmitrochenko.horsePuzzle.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.dialog.ConfirmSettingsDialog
import ru.dmitrochenko.horsePuzzle.model.BoardSettingsData
import ru.dmitrochenko.horsePuzzle.model.CheckBoardModel

class BoardSettings() : AppCompatActivity() {

    private lateinit var nextBtn: Button
    private lateinit var countRowsSeekBar: SeekBar
    private lateinit var countColsSeekBar: SeekBar
    private lateinit var countHintsSeekBar: SeekBar
    private lateinit var countRowsText: TextView
    private lateinit var countColsText: TextView
    private lateinit var countHintsText: TextView
    private lateinit var finishOnStartCheckBox: CheckBox

    private val boardSettings : BoardSettingsData = BoardSettingsData();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board_settings)

        initViews()
        setCountRowText()
        setCountColumnText()
        setHintText()

        nextBtn.setOnClickListener {
            if (boardSettings.selfCheck()) {
                openCheckBoardActivity()
            } else {
                val confirmDialog = ConfirmSettingsDialog()
                confirmDialog.show(supportFragmentManager, "confirmSettingsDialog");
            }
        }

        countRowsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (seekBar.progress <3) seekBar.progress = 3
                boardSettings.rows = seekBar.progress
                setCountRowText()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        countColsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (seekBar.progress <3) seekBar.progress = 3
                boardSettings.cols = seekBar.progress
                setCountColumnText()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        countHintsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                boardSettings.hints = seekBar.progress
                setHintText()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })

        finishOnStartCheckBox.setOnClickListener{
            boardSettings.finishOnStart = (it as CheckBox).isChecked
        }
    }

    fun openCheckBoardActivity() {
        intent = Intent(this, CheckBoard::class.java)
        intent.putExtra("EXTRA", boardSettings)
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        countRowsSeekBar = findViewById(R.id.countOfRowsSeekBar)
        countColsSeekBar = findViewById(R.id.countOfColumnsSeekBar)
        countHintsSeekBar = findViewById(R.id.countOfHintsSeekBar)
        countRowsText = findViewById(R.id.countOfRowsText)
        countColsText = findViewById(R.id.countOfColumnText)
        countHintsText = findViewById(R.id.countOfHintsText)
        finishOnStartCheckBox = findViewById(R.id.finishOnStart)
        nextBtn = findViewById(R.id.nextBtn)
    }

    private fun setHintText() {
        val text = when(boardSettings.hints) {
            0 -> getString(R.string.without_hints)
            13 -> getString(R.string.not_limited)
            else -> boardSettings.hints.toString()
        }
        countHintsText.text = text
    }

    private fun setCountColumnText() {
        val letter = CheckBoardModel.getColumnLetter(boardSettings.cols-1)
        countColsText.text = getString(R.string.count_of_columns_text, boardSettings.cols.toString(), letter)
    }

    private fun setCountRowText() {
        countRowsText.text = getString(R.string.count_of_rows_text, boardSettings.rows.toString())
    }
}


