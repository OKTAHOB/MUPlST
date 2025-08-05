package com.example.playlistmaker.features.search.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.features.player.presentation.ui.PlayerActivity
import com.example.playlistmaker.features.search.domain.model.Track
import com.example.playlistmaker.features.search.presentation.adapter.SearchAdapter
import com.example.playlistmaker.features.search.presentation.viewmodel.SearchState
import com.example.playlistmaker.features.search.presentation.viewmodel.SearchViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNoResults: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: SearchAdapter
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyContainer: LinearLayout
    private lateinit var progressBar: ProgressBar

    private val viewModel: SearchViewModel by viewModel()
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var currentDebounceText = ""

    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupViews()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupViews() {
        inputEditText = findViewById(R.id.inputEditText)
        clearIcon = findViewById(R.id.clearIcon)
        recyclerView = findViewById(R.id.recyclerView)
        placeholderNoResults = findViewById(R.id.placeholder_no_res)
        placeholderError = findViewById(R.id.placeholder_on_err)
        retryButton = findViewById(R.id.retryButton)
        historyTitle = findViewById(R.id.historyTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyContainer = findViewById(R.id.historyContainer)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        progressBar = findViewById(R.id.progressBar)
        val backArrow = findViewById<MaterialToolbar>(R.id.top_toolbar_frame)

        backArrow.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = SearchAdapter(emptyList()) { track ->
            viewModel.addTrackToHistory(track)
            openPlayerActivity(track)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        searchAdapter = SearchAdapter(emptyList()) { track ->
            viewModel.addTrackToHistory(track)
            openPlayerActivity(track)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter
    }

    private fun setupListeners() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    viewModel.loadSearchHistory()
                } else {
                    currentDebounceText = s.toString()
                    handler.removeCallbacksAndMessages(null)
                    searchRunnable = Runnable { viewModel.searchTracks(currentDebounceText) }
                    handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTracks(inputEditText.text.toString().trim())
                handler.removeCallbacksAndMessages(null)
                true
            } else {
                false
            }
        }

        retryButton.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            viewModel.retrySearch()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                clearIcon.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })

        clearIcon.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            inputEditText.text.clear()
            hideKeyboard()
            viewModel.loadSearchHistory()
            progressBar.visibility = View.GONE
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun observeViewModel() {
        viewModel.searchState.observe(this) { state ->
            when (state) {
                is SearchState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    placeholderNoResults.visibility = View.GONE
                    placeholderError.visibility = View.GONE
                    historyContainer.visibility = View.GONE
                }
                is SearchState.Success -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    historyContainer.visibility = View.GONE
                    placeholderNoResults.visibility = View.GONE
                    placeholderError.visibility = View.GONE
                    searchAdapter.updateData(state.tracks)
                }
                is SearchState.NoResults -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    historyContainer.visibility = View.GONE
                    placeholderNoResults.visibility = View.VISIBLE
                    placeholderError.visibility = View.GONE
                }
                is SearchState.Error -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    historyContainer.visibility = View.GONE
                    placeholderNoResults.visibility = View.GONE
                    placeholderError.visibility = View.VISIBLE
                }
                is SearchState.ShowHistory -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    historyContainer.visibility = View.VISIBLE
                    placeholderNoResults.visibility = View.GONE
                    placeholderError.visibility = View.GONE
                    historyAdapter.updateData(state.history)
                }
                is SearchState.Empty -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    historyContainer.visibility = View.GONE
                    placeholderNoResults.visibility = View.GONE
                    placeholderError.visibility = View.GONE
                }
            }
        }


    }

    private fun openPlayerActivity(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("TRACK_JSON", Gson().toJson(track))
        }
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, inputEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        inputEditText.setText(savedQuery)
        if (savedQuery?.isNotEmpty() == true) {
            viewModel.searchTracks(savedQuery)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
} 