package com.example.zoom.presentation.contacts

import com.example.zoom.data.DataRepository

class ContactsPresenter(
    private val view: ContactsContract.View
) : ContactsContract.Presenter {
    override fun loadData() {
        val contacts = DataRepository.getContacts()
            .sortedBy { it.username.lowercase() }
            .map { user ->
                ContactListItemUi(
                    userId = user.userId,
                    name = user.username,
                    email = user.email.orEmpty(),
                    phone = user.phone.orEmpty(),
                    initials = user.username
                        .split(" ")
                        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                        .take(2)
                        .joinToString("")
                        .ifBlank { "?" }
                )
            }
        view.showContent(
            ContactsUiState(
                totalCount = contacts.size,
                contacts = contacts
            )
        )
    }
}
