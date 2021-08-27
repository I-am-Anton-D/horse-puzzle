package ru.dmitrochenko.horsePuzzle.activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.CheckBoard
import java.lang.IllegalArgumentException

class ConfirmStartFieldDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val fieldName = arguments?.getString("FIELD_NAME") ?: throw IllegalArgumentException("FIELD_NAME not set")
            val fieldId = arguments?.getInt("FIELD_ID") ?: throw IllegalArgumentException("FIELD_ID not set")
            val combinationExist = arguments?.getBoolean("COMBINATION_EXIST") ?: throw IllegalArgumentException("COMBINATION_EXIST not set")
            builder.setTitle(getString(R.string.confirm_start_field))
                .setMessage(if (combinationExist) getString(R.string.choose_msg, fieldName) else getString(R.string.no_combintaion_from_start_field))
                .setCancelable(true)
                .setPositiveButton(if (combinationExist) getString(R.string.correct) else getString(R.string.tryw)) { _, _ ->
                    (activity as CheckBoard).setStartField(fieldId)
                }
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { _, _ -> }
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}