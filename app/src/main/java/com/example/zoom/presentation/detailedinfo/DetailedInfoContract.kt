package com.example.zoom.presentation.detailedinfo

import com.example.zoom.model.User

interface DetailedInfoContract {
    interface View {
        fun showContent(content: DetailedInfoUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class DetailedInfoUiState(
    val user: User,
    val sections: List<DetailedInfoSection>
)

data class DetailedInfoSection(
    val title: String,
    val rows: List<DetailedInfoRow>
)

data class DetailedInfoRow(
    val title: String,
    val value: String? = null,
    val showChevron: Boolean = false,
    val kind: DetailedInfoRowKind = DetailedInfoRowKind.Value
)

enum class DetailedInfoRowKind {
    Value,
    Link
}
