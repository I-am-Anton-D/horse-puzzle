package ru.dmitrochenko.horsePuzzle.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import ru.dmitrochenko.horsePuzzle.R
import ru.dmitrochenko.horsePuzzle.activity.dialog.AboutDialog
import ru.dmitrochenko.horsePuzzle.activity.dialog.ConfirmSettingsDialog

class MainActivity : AppCompatActivity() {
    private lateinit var newGameBtn:Button
    private lateinit var aboutBtn:Button
    private lateinit var exitBtn:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        newGameBtn = findViewById(R.id.newGame)
        aboutBtn = findViewById(R.id.about)
        exitBtn = findViewById(R.id.exit)

        aboutBtn.setOnClickListener{
            val aboutDialog = AboutDialog()
            aboutDialog.show(supportFragmentManager, "aboutDialog")
        }

        newGameBtn.setOnClickListener{
            val intent = Intent(this, BoardSettings::class.java)
            startActivity(intent)
        }

        exitBtn.setOnClickListener{
            this.finish()
        }
    }
}