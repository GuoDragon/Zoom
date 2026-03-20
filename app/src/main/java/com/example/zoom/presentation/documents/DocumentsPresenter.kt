package com.example.zoom.presentation.documents

class DocumentsPresenter(private val view: DocumentsContract.View) : DocumentsContract.Presenter {
    override fun loadData() {
        view.showEmpty()
    }
}
