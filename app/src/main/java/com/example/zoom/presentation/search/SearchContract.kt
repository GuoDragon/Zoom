package com.example.zoom.presentation.search

interface SearchContract {
    interface View {
        fun showContent(content: SearchUiState)
    }

    interface Presenter {
        fun loadData()
        fun onQueryChanged(query: String)
        fun onCategorySelected(category: SearchCategory)
        fun onFilterChipClicked(filterId: String)
        fun onSheetSearchChanged(query: String)
        fun onSheetOptionSelected(groupIndex: Int, optionId: String)
        fun onSheetReset()
        fun onSheetDone()
        fun onSheetDismiss()
    }
}
