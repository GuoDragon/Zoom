package com.example.zoom.presentation.documents

interface DocumentsContract {
    interface View {
        fun showUiState(state: DocumentsUiState)
    }
    interface Presenter {
        fun loadData()
    }
}

data class DocumentsUiState(
    val currentUserInitial: String,
    val isEmpty: Boolean
)
