package com.example.playlistmaker.features.media.presentation.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.features.media.presentation.viewmodel.CreatePlaylistEvent
import com.example.playlistmaker.features.media.presentation.viewmodel.CreatePlaylistViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.launch

class CreatePlaylistFragment : Fragment() {

    private val viewModel: CreatePlaylistViewModel by viewModel()

    private lateinit var backButton: ImageButton
    private lateinit var coverImageView: ImageView
    private lateinit var nameEditText: TextInputEditText
    private lateinit var descriptionEditText: TextInputEditText
    private lateinit var createButton: MaterialButton

    private var selectedCoverUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedCoverUri = uri
                coverImageView.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupListeners()
        observeEvents()
        handleSystemBack()
    }

    private fun bindViews(view: View) {
        backButton = view.findViewById(R.id.backButton)
        coverImageView = view.findViewById(R.id.coverImageView)
        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        createButton = view.findViewById(R.id.createButton)
    }

    private fun setupListeners() {
        nameEditText.doOnTextChanged { text, _, _, _ ->
            createButton.isEnabled = !text.isNullOrBlank()
        }

        backButton.setOnClickListener {
            handleExit()
        }

        coverImageView.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        createButton.setOnClickListener {
            val name = nameEditText.text?.toString().orEmpty()
            val description = descriptionEditText.text?.toString()
            viewModel.createPlaylist(name, description, selectedCoverUri?.toString())
        }
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is CreatePlaylistEvent.Success -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.playlist_created_message, event.playlistName),
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().navigateUp()
                        }
                        CreatePlaylistEvent.Error -> {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.playlist_creation_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleSystemBack() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleExit()
                }
            }
        )
    }

    private fun handleExit() {
        if (hasUnsavedChanges()) {
            showExitConfirmation()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        val hasName = !nameEditText.text.isNullOrBlank()
        val hasDescription = !descriptionEditText.text.isNullOrBlank()
        return hasName || hasDescription || selectedCoverUri != null
    }

    private fun showExitConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_playlist_exit_title)
            .setMessage(R.string.confirm_playlist_exit_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_finish) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }
}
