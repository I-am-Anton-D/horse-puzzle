package ru.dmitrochenko.horsePuzzle.activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.BoardSettings

class ConfirmSettingsDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.confirm_settings))
                .setMessage(getString(R.string.bad_settings))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.tryw)) { _, _ ->
                    (activity as BoardSettings).openCheckBoardActivity()
                }
                .setNegativeButton(getString(R.string.cancel)
                ) { _, _ -> }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}