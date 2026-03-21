package com.example.zoom.presentation.detailedinfo

import com.example.zoom.data.DataRepository

class DetailedInfoPresenter(
    private val view: DetailedInfoContract.View
) : DetailedInfoContract.Presenter {
    override fun loadData() {
        val user = DataRepository.getCurrentUser()
        view.showContent(
            DetailedInfoUiState(
                user = user,
                sections = listOf(
                    DetailedInfoSection(
                        title = "PERSONAL",
                        rows = listOf(
                            DetailedInfoRow("Display name", user.username, showChevron = true),
                            DetailedInfoRow("Account", user.email ?: "Not set", showChevron = true),
                            DetailedInfoRow("Department", "Not set"),
                            DetailedInfoRow("Job title", "Not set"),
                            DetailedInfoRow("Location", "Not set")
                        )
                    ),
                    DetailedInfoSection(
                        title = "CONTACT INFO",
                        rows = listOf(
                            DetailedInfoRow(
                                title = "Copy my direct chat link",
                                kind = DetailedInfoRowKind.Link
                            )
                        )
                    ),
                    DetailedInfoSection(
                        title = "MEETINGS",
                        rows = listOf(
                            DetailedInfoRow("Personal meeting ID (PMI)", "994 888 1080"),
                            DetailedInfoRow("Default call-in country or region", "Not set", showChevron = true),
                            DetailedInfoRow("Licenses", showChevron = true),
                            DetailedInfoRow("Restore purchase")
                        )
                    ),
                    DetailedInfoSection(
                        title = "SECURITY",
                        rows = listOf(
                            DetailedInfoRow("Where you're logged in", "3", showChevron = true)
                        )
                    )
                )
            )
        )
    }
}
