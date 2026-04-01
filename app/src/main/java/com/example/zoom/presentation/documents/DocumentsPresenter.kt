package com.example.zoom.presentation.documents

import com.example.zoom.data.DataRepository

class DocumentsPresenter(private val view: DocumentsContract.View) : DocumentsContract.Presenter {
    override fun loadData() {
        val currentUserInitial = DataRepository.getCurrentUser()
            .username
            .firstOrNull()
            ?.uppercase()
            ?: "?"
        view.showUiState(
            DocumentsUiState(
                currentUserInitial = currentUserInitial,
                isEmpty = true
            )
        )
    }
}
