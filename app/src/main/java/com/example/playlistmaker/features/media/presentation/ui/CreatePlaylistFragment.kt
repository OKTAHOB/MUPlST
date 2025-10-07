package com.example.playlistmaker.features.media.presentation.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

open class CreatePlaylistFragment : Fragment() {

    protected open val viewModel: CreatePlaylistViewModel by viewModel()

    protected lateinit var backButton: ImageButton
    protected lateinit var coverImageView: ImageView
    protected lateinit var nameEditText: TextInputEditText
    protected lateinit var descriptionEditText: TextInputEditText
    protected lateinit var createButton: MaterialButton
    protected lateinit var titleTextView: TextView

    protected var selectedCoverUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                applyCoverUri(uri)
                onCoverChanged()
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
        configureUi()
        setupListeners()
        observeEvents()
        handleSystemBack()
        onFragmentReady()
    }

    protected open fun bindViews(view: View) {
        backButton = view.findViewById(R.id.backButton)
        coverImageView = view.findViewById(R.id.coverImageView)
        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        createButton = view.findViewById(R.id.createButton)
        titleTextView = view.findViewById(R.id.titleTextView)
    }

    protected open fun configureUi() {
        titleTextView.setText(getTitleTextRes())
        createButton.setText(getActionButtonTextRes())
    }

    protected open fun setupListeners() {
        nameEditText.doOnTextChanged { text, _, _, _ ->
            createButton.isEnabled = !text.isNullOrBlank()
        }
        createButton.isEnabled = !nameEditText.text.isNullOrBlank()

        backButton.setOnClickListener {
            handleExit()
        }

        coverImageView.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        createButton.setOnClickListener {
            val name = nameEditText.text?.toString().orEmpty()
            val description = descriptionEditText.text?.toString()
            onCreateButtonClicked(name, description, selectedCoverUri?.toString())
        }
    }

    protected open fun onCreateButtonClicked(name: String, description: String?, coverUri: String?) {
        viewModel.createPlaylist(name, description, coverUri)
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }

    protected open fun handleEvent(event: CreatePlaylistEvent) {
        when (event) {
            is CreatePlaylistEvent.Success -> onPlaylistSaved(event.playlistName)
            CreatePlaylistEvent.Error -> onPlaylistSaveError()
            CreatePlaylistEvent.PlaylistNotFound -> onPlaylistNotFound()
        }
    }

    protected open fun onPlaylistSaved(playlistName: String) {
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_created_message, playlistName),
            Toast.LENGTH_SHORT
        ).show()
        findNavController().navigateUp()
    }

    protected open fun onPlaylistSaveError() {
        Toast.makeText(
            requireContext(),
            getString(R.string.playlist_creation_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    protected open fun onPlaylistNotFound() {
        onPlaylistSaveError()
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

    protected open fun handleExit() {
        if (hasUnsavedChanges()) {
            showExitConfirmation()
        } else {
            findNavController().navigateUp()
        }
    }

    protected open fun hasUnsavedChanges(): Boolean {
        val hasName = !nameEditText.text.isNullOrBlank()
        val hasDescription = !descriptionEditText.text.isNullOrBlank()
        return hasName || hasDescription || selectedCoverUri != null
    }

    protected open fun showExitConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_playlist_exit_title)
            .setMessage(R.string.confirm_playlist_exit_message)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_finish) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    protected open fun onCoverChanged() = Unit

    protected fun applyCoverUri(uri: Uri?) {
        selectedCoverUri = uri
        displayCover(uri)
    }

    protected open fun displayCover(uri: Uri?) {
        if (uri != null) {
            coverImageView.setImageURI(uri)
        } else {
            coverImageView.setImageResource(R.drawable.background_for_adding_photo)
        }
    }

    protected open fun onFragmentReady() = Unit

    protected open fun getTitleTextRes(): Int = R.string.new_playlist

    protected open fun getActionButtonTextRes(): Int = R.string.create
}
