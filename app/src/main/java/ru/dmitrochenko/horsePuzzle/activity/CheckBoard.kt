package ru.dmitrochenko.horsePuzzle.activity

import android.content.res.Resources
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.lifecycle.ViewModelProvider
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.dialog.ConfirmStartFieldDialog
import ru.dmitrochenko.horsePuzzle.activity.view.Field
import ru.dmitrochenko.horsePuzzle.model.BoardSettingsData
import ru.dmitrochenko.horsePuzzle.model.CheckBoardModel


class CheckBoard : AppCompatActivity() {
    private var blackColor = 0
    private var whiteColor = 0
    private var orangeColor = 0

    private lateinit var grid: GridLayout
    private lateinit var path: TextView
    private lateinit var availText: TextView
    private lateinit var backBtn: Button
    private lateinit var forwardBtn: Button
    private lateinit var hintBtn: Button

    private val boardModel: CheckBoardModel by lazy {
        ViewModelProvider(this).get(CheckBoardModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_board)

        initColors()
        initSettings()
        initBoard()
        initPath()
        initCommandButtons()
    }

    private fun initColors() {
        blackColor = ContextCompat.getColor(applicationContext, R.color.grey)
        whiteColor = ContextCompat.getColor(applicationContext, R.color.white)
        orangeColor = ContextCompat.getColor(applicationContext, R.color.orange)
    }

    private fun initSettings() {
        val boardSettings: BoardSettingsData? = intent.getParcelableExtra("EXTRA")
        if (boardSettings != null) {
            boardModel.applySettings(boardSettings)
        }
    }

    private fun initPath() {
        if (boardModel.moves.size > 0) {
            showPath()
        }
    }

    private fun initBoard() {
        grid = findViewById(R.id.board)
        grid.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                grid.viewTreeObserver.removeOnGlobalLayoutListener(this)
                for (row in 0 until boardModel.rows) {
                    for (col in 0 until boardModel.cols) {
                        val index = row * boardModel.cols + col
                        val nButton = getNewButton(index, row, col)
                        grid.addView(nButton, index)
                    }
                }
            }
        })
    }

    private fun initCommandButtons() {
        path = findViewById(R.id.path)

        backBtn = findViewById(R.id.leftBtn)
        backBtn.setOnClickListener {
            moveBack()
        }
        forwardBtn = findViewById(R.id.rightBtn)
        forwardBtn.setOnClickListener {
            moveForward()
        }
        hintBtn = findViewById(R.id.hintBtn)
        hintBtn.text = getHintText()
    }

    private fun hintClick() {
        if (boardModel.hints != 0 && boardModel.hints != 13) {
            boardModel.hints--
            hintBtn.text = getHintText()
        }

        if (boardModel.hints == 0) {
            hintBtn.isEnabled = false
        }
    }

    private fun getHintText(): String {
        return if (boardModel.hints == 13) {
            getString(R.string.hint)
        } else {
            getString(R.string.hint) + "(${boardModel.hints})"
        }
    }

    private fun fieldClick(): (v: View) -> Unit = {
        if (boardModel.currentIndex == -1) {
            openConfirmStartFieldDialog(it)
        }

        if (boardModel.currentIndex >= 0 && boardModel.isFieldAvailable(it.id)) {
            makeMove(it.id)
        }
    }

    private fun makeMove(position: Int) {
        setHorse(boardModel.getCurrentPosition())
        boardModel.makeMove(position)
        setActiveHorse(position)
    }

    private fun moveForward() {
        if (!boardModel.stayOnLast()) {
            setHorse(boardModel.getCurrentPosition())
            boardModel.moveForward()
            setActiveHorse(boardModel.getCurrentPosition())
        }
    }

    private fun moveBack() {
        if (boardModel.currentIndex > 0) {
            unSetHorse(boardModel.getCurrentPosition())
            boardModel.moveBack()
            setActiveHorse(boardModel.getCurrentPosition())
        }
    }

    private fun showPath() {
        val pathText = SpannableStringBuilder().append(getPathBackText())
            .color(orangeColor) { append(getPathActiveText()) }
            .color(blackColor) { append(getPathForwardText()) }
        path.text = pathText
    }

    private fun getPathBackText(): String {
        var text = ""
        for (i in 0 until boardModel.currentIndex) {
            text += (i + 1).toString() + "." + boardModel.getFieldNameById(boardModel.moves[i]) + " "
        }
        return text
    }

    private fun getPathActiveText(): String {
        return (boardModel.currentIndex + 1).toString() + "." +
                boardModel.getFieldNameById(boardModel.getCurrentPosition()) + " "
    }

    private fun getPathForwardText(): String {
        var text = ""
        for (i in boardModel.currentIndex + 1 until boardModel.moves.size) {
            text += (i + 1).toString() + "." + boardModel.getFieldNameById(boardModel.moves[i]) + " "
        }
        return text
    }

    fun setStartField(position: Int) {
        boardModel.setStartPosition(position)
        setActiveHorse(position)
        val chooser: TextView = findViewById(R.id.chooseStartField)
        chooser.visibility = GONE
        showPath()

        if (boardModel.hints != 0) {
            hintBtn.isEnabled = true
            hintBtn.setOnClickListener { hintClick() }
        }

        availText = findViewById(R.id.availCombination)
        availText.visibility = VISIBLE
    }



    private fun setActiveHorse(position: Int) {
        (grid.getChildAt(position) as Field).setActiveHorse()
        showPath()
    }

    private fun setHorse(position: Int) {
        (grid.getChildAt(position) as Field).setHorse()
    }

    private fun unSetHorse(position: Int) {
        (grid.getChildAt(position) as Field).unSetHorse()
    }

    private fun unMarkField(id: Int) {
        (grid.getChildAt(id) as Field).umMark()
    }

    private fun getNewButton(index: Int, row: Int, col: Int): Button {
        val cellDim = calculateCellDim()
        return Field(grid.context).apply {
            id = index
            text = CheckBoardModel.getFieldText(row, col)
            textSize = cellDim / 12F
            gravity = Gravity.START or Gravity.BOTTOM
            setPadding(cellDim / 20, 0, 0, 0)

            if (CheckBoardModel.isWhite(row, col)) {
                fieldColor = whiteColor
                fieldTextColor = blackColor
            } else {
                fieldColor = blackColor
                fieldTextColor = whiteColor
            }

            markColor = orangeColor
            horseDrw = R.drawable.ic_h3
            horseActiveDrw = R.drawable.ic_h3_active

            layoutParams = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(boardModel.rows - row - 1)
                columnSpec = GridLayout.spec(col)
                height = cellDim
                width = cellDim
            }
            setOnClickListener(fieldClick())
        }
    }

    private fun calculateCellDim(): Int {
        val screen = findViewById<View>(R.id.boardScreen)
        val width = screen.width
        val margin = (12 * Resources.getSystem().displayMetrics.density).toInt()
        return if (boardModel.rows > boardModel.cols) {
            width / boardModel.rows
        } else {
            (width - margin) / boardModel.cols
        }
    }

    private fun openConfirmStartFieldDialog(it: View) {
        val confirmDialog = ConfirmStartFieldDialog()
        confirmDialog.arguments = Bundle().apply {
            putString("FIELD_NAME", boardModel.getFieldNameById(it.id))
            putInt("FIELD_ID", it.id)
        }
        confirmDialog.show(supportFragmentManager, "confirmStartFieldDialog")
    }
}