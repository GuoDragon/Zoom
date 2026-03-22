package com.example.zoom.presentation.search

interface SearchContract {
    interface View {
        fun showContent(content: SearchUiState)
    }

    interface Presenter {
        fun loadData()
        fun search(query: String)
    }
}
