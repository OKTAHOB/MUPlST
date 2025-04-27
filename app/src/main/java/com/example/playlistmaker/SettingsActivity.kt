package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backArrow = findViewById<MaterialToolbar>(R.id.settings_appbar)

        backArrow.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        val btnShare = findViewById<MaterialTextView>(R.id.btn_share)
        btnShare.setOnClickListener {

            val shareText = getString(R.string.course_url)

            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }

            val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_via))
            startActivity(shareIntent)
        }

        val btnContactSupport = findViewById<MaterialTextView>(R.id.btn_contact_support)

        btnContactSupport.setOnClickListener {
            val email = getString(R.string.sup_mail)
            val subject = getString(R.string.message_to_sup_h)
            val body = getString(R.string.message_to_sup)

            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }
            startActivity(intent)
        }
        val btnTerms = findViewById<MaterialTextView>(R.id.btn_terms_of_use)

        btnTerms.setOnClickListener {

            val termsUrl = getString(R.string.terms_url)

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl))
            startActivity(intent)
        }
    }
}