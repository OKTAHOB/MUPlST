package com.example.playlistmaker.presentation.activity

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.usecase.GetThemeSettingsUseCase
import com.example.playlistmaker.domain.usecase.SaveThemeSettingsUseCase
import com.example.playlistmaker.presentation.util.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    private lateinit var switchMaterial: SwitchMaterial
    private var isSystemThemeUpdate = false
    private lateinit var getThemeSettingsUseCase: GetThemeSettingsUseCase
    private lateinit var saveThemeSettingsUseCase: SaveThemeSettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        getThemeSettingsUseCase = Creator.provideGetThemeSettingsUseCase(this)
        saveThemeSettingsUseCase = Creator.provideSaveThemeSettingsUseCase(this)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.settings_switch)
        themeSwitcher.isChecked = getThemeSettingsUseCase.execute()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            saveThemeSettingsUseCase.execute(checked)
            AppCompatDelegate.setDefaultNightMode(
                if (checked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        isSystemThemeUpdate = true
        updateSwitchState()
        isSystemThemeUpdate = false
    }

    private fun updateSwitchState() {
        val currentNightMode = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        val isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        switchMaterial.isChecked = isDarkTheme
    }

    private fun setAppTheme(isDark: Boolean) {
        val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES
        else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        delegate.applyDayNight()
    }
}