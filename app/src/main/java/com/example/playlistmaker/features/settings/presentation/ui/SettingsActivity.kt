package com.example.playlistmaker.features.settings.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.features.settings.presentation.viewmodel.SettingsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.settings_switch)
        
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        setupToolbar()
        setupShareButton()
        setupSupportButton()
        setupTermsButton()
    }

    private fun observeViewModel() {
        viewModel.themeSettings.observe(this) { isDarkTheme ->
            findViewById<SwitchMaterial>(R.id.settings_switch).isChecked = isDarkTheme
        }
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.settings_appbar).setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupShareButton() {
        findViewById<MaterialTextView>(R.id.btn_share).setOnClickListener {
            val shareText = getString(R.string.course_url)
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }.let { sendIntent ->
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_via)))
            }
        }
    }

    private fun setupSupportButton() {
        findViewById<MaterialTextView>(R.id.btn_contact_support).setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.sup_mail)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_to_sup_h))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.message_to_sup))
            }.let { intent ->
                startActivity(intent)
            }
        }
    }

    private fun setupTermsButton() {
        findViewById<MaterialTextView>(R.id.btn_terms_of_use).setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.terms_url))
                )
            )
        }
    }
} 