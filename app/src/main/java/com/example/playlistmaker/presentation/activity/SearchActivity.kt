package com.example.playlistmaker.presentation.activity

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.usecase.interactor.SearchInteractor
import com.example.playlistmaker.presentation.adapter.Adapter
import com.example.playlistmaker.presentation.util.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchInteractor: SearchInteractor
    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private var currentSearchText: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNoResults: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: Adapter
    private lateinit var searchAdapter: Adapter
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyContainer: LinearLayout
    private lateinit var progressBar: ProgressBar

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

        searchInteractor = Creator.provideSearchInteractor(this)

        setupViews()
        setupRecyclerView()
        setupListeners()
        checkHistoryVisibility()
    }

    private fun hideSearchHistory() {
        historyContainer.visibility = View.GONE
    }

    private fun clearSearchResults() {
        searchAdapter.updateData(emptyList())
        recyclerView.visibility = View.GONE
        showSearchHistory()
    }

    private fun checkHistoryVisibility() {
        if (inputEditText.text.isNullOrEmpty()) {
            showSearchHistory()
        }
    }

    fun clearHistory(view: View) {
        searchInteractor.clearSearchHistory()
        historyAdapter.updateData(emptyList())
        checkHistoryVisibility()
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
        historyAdapter = Adapter(emptyList()) { track ->
            searchInteractor.addTrackToHistory(track)
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                putExtra("TRACK_JSON", Gson().toJson(track))
            }
            startActivity(intent)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        searchAdapter = Adapter(emptyList()) { track ->
            searchInteractor.addTrackToHistory(track)
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java).apply {
                putExtra("TRACK_JSON", Gson().toJson(track))
            }
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter
    }

    private fun setupListeners() {
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    showSearchHistory()
                } else {
                    hideSearchHistory()
                    currentDebounceText = s.toString()
                    handler.removeCallbacksAndMessages(null)
                    searchRunnable = Runnable { performSearch(currentDebounceText) }
                    handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString().trim())
                handler.removeCallbacksAndMessages(null)
                true
            } else {
                false
            }
        }

        retryButton.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            performSearch(currentSearchText)
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
            clearSearchResults()
            progressBar.visibility = View.GONE
        }
    }

    private fun performSearch(str: String) {
        if (str.isEmpty()) {
            showSearchHistory()
            return
        }
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderNoResults.visibility = View.GONE
        placeholderError.visibility = View.GONE

        currentSearchText = str
        hideKeyboard()

        lifecycleScope.launch {
            try {
                val tracks = searchInteractor.searchTracks(str)
                if (tracks.isNotEmpty()) {
                    searchAdapter.updateData(tracks)
                    showSearchResults()
                } else {
                    showNoResults()
                }
            } catch (e: Exception) {
                showError()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showSearchResults() {
        recyclerView.visibility = View.VISIBLE
        historyContainer.visibility = View.GONE
        placeholderNoResults.visibility = View.GONE
        placeholderError.visibility = View.GONE
    }

    private fun showNoResults() {
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholderNoResults.visibility = View.VISIBLE
        placeholderError.visibility = View.GONE
    }

    private fun showError() {
        recyclerView.visibility = View.GONE
        historyContainer.visibility = View.GONE
        placeholderNoResults.visibility = View.GONE
        placeholderError.visibility = View.VISIBLE
    }

    private fun showSearchHistory() {
        val history = searchInteractor.getSearchHistory()
        if (history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
            historyAdapter.updateData(history)
            recyclerView.visibility = View.GONE
            placeholderNoResults.visibility = View.GONE
            placeholderError.visibility = View.GONE
            progressBar.visibility = View.GONE
        } else {
            hideSearchHistory()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, inputEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedQuery = savedInstanceState.getString(SEARCH_QUERY_KEY, "")
        inputEditText.setText(savedQuery)
        if (savedQuery?.isNotEmpty() == true) performSearch(savedQuery)
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