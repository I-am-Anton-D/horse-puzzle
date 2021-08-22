package ru.dmitrochenko.horsePuzzle.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import ru.dmitrochenko.horsePuzzle.R

class MainActivity : AppCompatActivity() {
    private lateinit var newGameBtn:Button
    private lateinit var settingBtn:Button
    private lateinit var aboutBtn:Button
    private lateinit var exitBtn:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newGameBtn = findViewById(R.id.newGame)
        settingBtn = findViewById(R.id.settings)
        aboutBtn = findViewById(R.id.about)
        exitBtn = findViewById(R.id.exit)

        settingBtn.setOnClickListener{
            Toast.makeText(this, "Setting button", Toast.LENGTH_SHORT).show()
        }

        aboutBtn.setOnClickListener{
            Toast.makeText(this, "About button", Toast.LENGTH_SHORT).show()
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