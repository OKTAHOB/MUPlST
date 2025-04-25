package com.example.playlistmaker

import android.annotation.SuppressLint

import android.os.Bundle
import android.view.View
import android.widget.Button



import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById<LinearLayout>(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        val srch_btn = findViewById<Button>(R.id.srch_btn)
        val srch_btnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                Toast.makeText(this@MainActivity, "Poisk", Toast.LENGTH_SHORT).show()
            }

        }
        srch_btn.setOnClickListener(srch_btnClickListener)

        val m_btn = findViewById<Button>(R.id.media_btn)

        m_btn.setOnClickListener {
            Toast.makeText(this@MainActivity, "media!", Toast.LENGTH_SHORT).show()
        }
        val set_btn = findViewById<Button>(R.id.set_btn)

        set_btn.setOnClickListener {
            Toast.makeText(this@MainActivity, "settings!", Toast.LENGTH_SHORT).show()
        }
    }
}