package com.example.zoom.presentation.documents

interface DocumentsContract {
    interface View {
        fun showEmpty()
    }
    interface Presenter {
        fun loadData()
    }
}
