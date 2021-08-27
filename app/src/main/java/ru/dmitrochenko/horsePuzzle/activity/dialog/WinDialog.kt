package ru.dmitrochenko.horsePuzzle.activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.CheckBoard

class WinDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.congratulation))
                .setMessage(getString(R.string.you_win))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.showPath)) { _, _ ->
                    this.dismiss()
                    (activity as CheckBoard).showWinPath()
                }
                .setNegativeButton(getString(R.string.cancel)
                ) { _, _ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}