package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearIcon: ImageView
    private var currentSearchText: String = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderNoResults: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var retryButton: Button
    private var currentCall: Call<SearchResponse>? = null
    private lateinit var searchHistory: SearchHistory
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var historyAdapter: Adapter
    private lateinit var searchAdapter: Adapter
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchHistory = SearchHistory(getSharedPreferences("search_history", MODE_PRIVATE))


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
        searchHistory.clear()
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
        val backArrow = findViewById<MaterialToolbar>(R.id.top_toolbar_frame)


        backArrow.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        historyAdapter = Adapter(emptyList()) { track ->
            searchHistory.addTrack(track)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        searchAdapter = Adapter(emptyList()) { track ->
            searchHistory.addTrack(track)
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
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(inputEditText.text.toString().trim())
                true
            } else {
                false
            }
        }

        retryButton.setOnClickListener {
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
            inputEditText.text.clear()
            hideKeyboard()
            clearSearchResults()
        }
    }

    private fun performSearch(str: String) {
        if (str.isEmpty()) {
            showSearchHistory()
            return
        }
        currentSearchText = str
        hideKeyboard()

        val call = RetrofitClient.musicApiService.search(str)

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    if (searchResponse?.results?.isNotEmpty() == true) {
                        searchAdapter.updateData(searchResponse.results)
                        showSearchResults()
                    } else {
                        showNoResults()
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                showError()
            }
        })
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
        val history = searchHistory.getHistory()
        if (history.isNotEmpty()) {
            historyContainer.visibility = View.VISIBLE
            historyAdapter.updateData(history)
            recyclerView.visibility = View.GONE
            placeholderNoResults.visibility = View.GONE
            placeholderError.visibility = View.GONE
        } else {
            hideSearchHistory()
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_QUERY_KEY, inputEditText.text.toString())    }

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
    companion object {
        private const val SEARCH_QUERY_KEY = "search_query"
    }
    override fun onDestroy() {
        super.onDestroy()
        currentCall?.cancel()
    }

}