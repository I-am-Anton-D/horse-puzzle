package ru.dmitrochenko.horsePuzzle.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.dialog.ConfirmStartFieldDialog
import ru.dmitrochenko.horsePuzzle.model.BoardSettingsData
import ru.dmitrochenko.horsePuzzle.model.CheckBoardModel


class CheckBoard : AppCompatActivity() {
    private lateinit var grid: GridLayout
    private lateinit var pathText: TextView

    private val boardModel: CheckBoardModel by lazy {
        ViewModelProvider(this).get(CheckBoardModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_board)

        initSettings()
        initBoard()
        initPath()
    }

    private fun initSettings() {
        val boardSettings: BoardSettingsData? = intent.getParcelableExtra("EXTRA")
        if (boardSettings != null) {
            boardModel.applySettings(boardSettings)
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
                        val index = row * boardModel.cols + col;
                        val nButton = getNewButton(index, row, col)
                        grid.addView(nButton, index)
                    }
                }
            }
        })
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
                white = true
                fieldColor = ContextCompat.getColor(context, R.color.white)
                fieldTextColor = ContextCompat.getColor(context, R.color.grey)
            } else {
                white = false
                fieldColor = ContextCompat.getColor(context, R.color.grey)
                fieldTextColor = ContextCompat.getColor(context,  R.color.white)
            }

            markColor = ContextCompat.getColor(context, R.color.orange)
            horseDrw = R.drawable.ic_h2

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
        return if (boardModel.rows > boardModel.cols) {
            width / boardModel.rows
        } else {
            ((0.97 * width).toInt()) / boardModel.cols
        }
    }

    private fun fieldClick(): (v: View) -> Unit = {
        if (boardModel.start == -1) {
            (it as Field).mark()
            openConfirmStartFieldDialog(it)
        } else {
            if (boardModel.isFieldAvailable(it.id)) {
                setHorse(it.id)
            }
        }



    }

    private fun initPath() {
        if (boardModel.start >=0) {
            showPath()
        }
    }

    private fun showPath() {
        pathText = findViewById(R.id.pathText)
        pathText.textAlignment = TEXT_ALIGNMENT_TEXT_START
        pathText.text = "HERE PATH"
    }

    fun setStartField(id:Int) {
        boardModel.start = id
        setHorse(id)
        showPath()
    }

    private fun setHorse(id: Int) {
        (grid.getChildAt(id) as Field).setHorse()
    }

    fun cancelSetStartField(id:Int) {
         unMarkField(id)
    }

    private fun unMarkField(id: Int) {
        (grid.getChildAt(id) as Field).umMark()
    }

    private fun openConfirmStartFieldDialog(it: View) {
        val confirmDialog = ConfirmStartFieldDialog()
        confirmDialog.arguments = Bundle().apply {
            putString("FIELD_NAME", CheckBoardModel.getFieldNameById(it.id, boardModel.cols))
            putInt("FIELD_ID", it.id)
        }
        confirmDialog.show(supportFragmentManager, "confirmStartFieldDialog");
    }

}