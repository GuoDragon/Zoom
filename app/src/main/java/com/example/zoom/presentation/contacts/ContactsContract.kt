package com.example.zoom.presentation.contacts

interface ContactsContract {
    interface View {
        fun showContent(content: ContactsUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class ContactListItemUi(
    val userId: String,
    val name: String,
    val email: String,
    val phone: String,
    val initials: String
)

data class ContactsUiState(
    val totalCount: Int,
    val contacts: List<ContactListItemUi>
)
