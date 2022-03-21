package ru.dmitrochenko.horsePuzzle.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.google.android.material.snackbar.Snackbar
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.dialog.ConfirmStartFieldDialog
import ru.dmitrochenko.horsePuzzle.activity.dialog.WinDialog
import ru.dmitrochenko.horsePuzzle.activity.view.Field
import ru.dmitrochenko.horsePuzzle.model.BoardSettingsData
import ru.dmitrochenko.horsePuzzle.model.CheckBoardModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CheckBoard : AppCompatActivity() {
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private val boardModel: CheckBoardModel by lazy {
        ViewModelProvider(this).get(CheckBoardModel::class.java)
    }

    private var blackColor = 0
    private var whiteColor = 0
    private var orangeColor = 0
    private var yellowColor = 0
    private var greyColor = 0

    private var hintField = -1
    private var hintMove = false

    private lateinit var grid: GridLayout
    private lateinit var path: TextView
    private lateinit var availText: TextView
    private lateinit var backBtn: Button
    private lateinit var forwardBtn: Button
    private lateinit var hintBtn: Button

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
        blackColor = ContextCompat.getColor(applicationContext, R.color.blackField)
        whiteColor = ContextCompat.getColor(applicationContext, R.color.whiteField)
        orangeColor = ContextCompat.getColor(applicationContext, R.color.orange)
        yellowColor = ContextCompat.getColor(applicationContext, R.color.yellow)
        greyColor = ContextCompat.getColor(applicationContext, R.color.grey)
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
        availText = findViewById(R.id.availCombination)

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
        hintBtn.setOnClickListener { hintClick() }
    }

    private fun hintClick() {
        if (boardModel.hints != 0 && boardModel.hints != 13) {
            boardModel.hints--
            hintBtn.text = getHintText()
        }

        val hintsPositions = boardModel.getHintsPositions()
        if (hintsPositions.isNotEmpty()) {
            hintField = hintsPositions.random().toInt()
            markField(hintField)
        }

        hintBtn.isEnabled = false
    }

    private fun getHintText(): String {
        return if (boardModel.hints == 13) getString(R.string.hint) else getString(R.string.hint) + "(${boardModel.hints})"
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
        hintMove = boardModel.getHintsPositions().contains(position.toByte())
        setHorse(boardModel.getCurrentPosition())
        boardModel.makeMove(position)
        setActiveHorse(position)
    }

    private fun moveForward() {
        if (!boardModel.stayOnLast()) {
            hintMove = false
            setHorse(boardModel.getCurrentPosition())
            boardModel.moveForward()
            setActiveHorse(boardModel.getCurrentPosition())
        }
    }

    private fun moveBack() {
        if (boardModel.currentIndex > 0) {
            hintMove = false
            unSetHorse(boardModel.getCurrentPosition())
            boardModel.moveBack()
            setActiveHorse(boardModel.getCurrentPosition())
        }
    }

    private fun showPath() {
        val pathText = SpannableStringBuilder().append(getPathBackText())
            .color(orangeColor) { append(getPathActiveText()) }
            .color(greyColor) { append(getPathForwardText()) }
        path.text = pathText
        if (!boardModel.noHints && boardModel.getCountOfRemainingMoves() != 0) {
            getCombination()
        }
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
        val chooser: TextView = findViewById(R.id.chooseStartField)
        chooser.visibility = GONE
        availText.visibility = VISIBLE
        if (boardModel.finishOnStart) {
            (grid.getChildAt(position) as Field).fieldColor = yellowColor
        }
        setActiveHorse(position)
    }

    private fun getCombination() {
        availText.text = getString(R.string.calculate)
        hintBtn.isEnabled = false

        val full = boardModel.getCountOfRemainingMoves() < 32
        if (hintMove) {
            postCombination(1, true)
            return
        }

        if (!full && (!boardModel.checkBoard() || boardModel.getAvailablePositions().isEmpty())) {
            postCombination(0, true)
            return
        }

        boardModel.cancelCalculate()
        executor.execute {
            val count = boardModel.getCombinations()
            handler.post {
                if (count >= 0) postCombination(count, full)
            }
        }
    }

    private fun postCombination(count: Long, full: Boolean) {
        if (count == 0L) {
            availText.text = if (full) getString(R.string.no_combination) else getString(R.string.maybe_not)
        } else {
            hintBtn.isEnabled = boardModel.hints > 0
            availText.text = getString(R.string.available_combination)
        }
    }

    private fun setActiveHorse(position: Int) {
        unMarkField(hintField)
        hintField = -1
        (grid.getChildAt(position) as Field).setActiveHorse()

        if (boardModel.getCountOfRemainingMoves() == 0) {
            hintBtn.isEnabled = false
            if (boardModel.finishOnStart) {
                if (boardModel.canReachStart()) {
                    openWinDialog()
                } else {
                    availText.text = getString(R.string.need_to_reach_start_field)
                }
            } else {
                openWinDialog()
            }
        }

        showPath()
    }

    private fun setHorse(position: Int) {
        (grid.getChildAt(position) as Field).setHorse()
    }

    private fun unSetHorse(position: Int) {
        (grid.getChildAt(position) as Field).unSetHorse()
    }

    private fun unMarkField(position: Int) {
        if (position < 0) return
        (grid.getChildAt(position) as Field).unMark()
    }

    private fun markField(position: Int) {
        if (position < 0) return
        (grid.getChildAt(position) as Field).mark()
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
            horseDrw = R.drawable.chess_horse
            horseActiveDrw = R.drawable.chess_horse_active

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
        val margin = (0 * Resources.getSystem().displayMetrics.density).toInt()
        return if (boardModel.rows > boardModel.cols) {
            width / boardModel.rows
        } else {
            (width - margin) / boardModel.cols
        }
    }

    private fun openWinDialog() {
        availText.text = ""
        val winDialog = WinDialog()
        winDialog.show(supportFragmentManager, "winDialog")
    }

    private fun openConfirmStartFieldDialog(it: View) {
        var combinationExist = true
        if (boardModel.hints != 0) {
            combinationExist = boardModel.checkFirstField(it.id)
        }

        val confirmDialog = ConfirmStartFieldDialog()
        confirmDialog.arguments = Bundle().apply {
            putString("FIELD_NAME", boardModel.getFieldNameById(it.id))
            putInt("FIELD_ID", it.id)
            putBoolean("COMBINATION_EXIST", combinationExist)
        }
        confirmDialog.show(supportFragmentManager, "confirmStartFieldDialog")
    }

    fun copyPathToClipBoard() {
        val clipboard: ClipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java)!!
        val clip = ClipData.newPlainText(getString(R.string.copy_path), path.text)
        clipboard.setPrimaryClip(clip)

        Snackbar.make(grid, getString(R.string.path_copy_to_clip), Snackbar.LENGTH_LONG).show()
        handler.postDelayed({ goToMainMenu() }, 2000)
    }

    fun goToMainMenu() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}