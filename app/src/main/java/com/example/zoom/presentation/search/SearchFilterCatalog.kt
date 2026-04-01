package com.example.zoom.presentation.search

import java.util.Calendar

internal enum class SearchFilterDisplayMode {
    LabelWhenDefault,
    AlwaysSelection,
    StaticLabel
}

internal data class FilterOptionConfig(
    val id: String,
    val label: String,
    val leading: SearchSheetOptionLeading? = null
)

internal data class FilterGroupConfig(
    val style: SearchSheetGroupStyle,
    val options: List<FilterOptionConfig>,
    val defaultOptionId: String
)

internal data class FilterConfig(
    val id: String,
    val label: String,
    val chipStyle: SearchFilterChipStyle = SearchFilterChipStyle.Sheet,
    val displayMode: SearchFilterDisplayMode = SearchFilterDisplayMode.LabelWhenDefault,
    val showDone: Boolean = false,
    val showSearchField: Boolean = false,
    val groups: List<FilterGroupConfig> = emptyList()
)

internal data class FilterSelectionState(
    val selectedOptionIds: List<String> = emptyList(),
    val enabled: Boolean = false
)

internal object SearchFilterCatalog {
    fun build(currentUserInitial: String): Map<SearchCategory, List<FilterConfig>> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val resolvedUserInitial = currentUserInitial.ifBlank { "ME" }

        val commonDateOptions = listOf(
            FilterOptionConfig("any_time", "Any time"),
            FilterOptionConfig("today", "Today"),
            FilterOptionConfig("yesterday", "Yesterday"),
            FilterOptionConfig("last_7_days", "Last 7 days"),
            FilterOptionConfig("last_30_days", "Last 30 days"),
            FilterOptionConfig("this_year", "This year ($currentYear)"),
            FilterOptionConfig("custom_range", "Custom range")
        )

        val messageScopeOptions = listOf(
            FilterOptionConfig("all", "All", SearchSheetOptionLeading.Conversation),
            FilterOptionConfig("bookmarked", "Bookmarked messages", SearchSheetOptionLeading.Bookmark),
            FilterOptionConfig("my_space", "You", SearchSheetOptionLeading.Initials(resolvedUserInitial))
        )

        val peopleOptions = listOf(
            FilterOptionConfig("anyone", "Anyone", SearchSheetOptionLeading.Person),
            FilterOptionConfig("me", "You", SearchSheetOptionLeading.Initials(resolvedUserInitial))
        )

        val ownerOptions = listOf(
            FilterOptionConfig("anyone", "Anyone", SearchSheetOptionLeading.Conversation),
            FilterOptionConfig("me", resolvedUserInitial, SearchSheetOptionLeading.Initials(resolvedUserInitial))
        )

        return mapOf(
            SearchCategory.TopResults to emptyList(),
            SearchCategory.Messages to listOf(
                FilterConfig(
                    id = "message_sort",
                    label = "Sort by",
                    displayMode = SearchFilterDisplayMode.AlwaysSelection,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "most_relevant",
                            options = listOf(
                                FilterOptionConfig("most_relevant", "Most relevant"),
                                FilterOptionConfig("most_recent", "Most recent")
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "message_in",
                    label = "In",
                    showSearchField = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, messageScopeOptions, "all"))
                ),
                FilterConfig(
                    id = "message_from",
                    label = "From",
                    showSearchField = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, peopleOptions, "anyone"))
                ),
                FilterConfig(
                    id = "message_mentions",
                    label = "@",
                    showSearchField = true,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "anyone",
                            options = listOf(
                                FilterOptionConfig("anyone", "Anyone", SearchSheetOptionLeading.Conversation),
                                FilterOptionConfig("me", "You", SearchSheetOptionLeading.Initials(resolvedUserInitial))
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "message_date",
                    label = "Date",
                    showDone = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, commonDateOptions, "any_time"))
                )
            ),
            SearchCategory.Chats to listOf(
                FilterConfig(
                    id = "chat_sort",
                    label = "Sort type",
                    displayMode = SearchFilterDisplayMode.AlwaysSelection,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "most_relevant",
                            options = listOf(
                                FilterOptionConfig("most_relevant", "Most relevant"),
                                FilterOptionConfig("most_recent", "Most recent"),
                                FilterOptionConfig("fewest_members", "Fewest members"),
                                FilterOptionConfig("a_to_z", "A-Z")
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "chat_managed_by",
                    label = "Managed by",
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "anyone",
                            options = listOf(
                                FilterOptionConfig("anyone", "Anyone"),
                                FilterOptionConfig("me", "Me")
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "chat_type",
                    label = "Type",
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "any",
                            options = listOf(
                                FilterOptionConfig("any", "Any"),
                                FilterOptionConfig("channels", "Channels"),
                                FilterOptionConfig("group_chats", "Group chats"),
                                FilterOptionConfig("direct_messages", "Direct messages")
                            )
                        )
                    )
                )
            ),
            SearchCategory.Meetings to listOf(
                FilterConfig(
                    id = "meeting_date",
                    label = "Date",
                    showDone = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, commonDateOptions, "any_time"))
                ),
                FilterConfig(
                    id = "meeting_type",
                    label = "Type",
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "any",
                            options = listOf(
                                FilterOptionConfig("any", "Any"),
                                FilterOptionConfig("recurring", "Recurring"),
                                FilterOptionConfig("non_recurring", "Non-recurring")
                            )
                        )
                    )
                )
            ),
            SearchCategory.Contacts to emptyList(),
            SearchCategory.Files to listOf(
                FilterConfig(
                    id = "file_sort",
                    label = "Sort by",
                    displayMode = SearchFilterDisplayMode.AlwaysSelection,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "most_relevant",
                            options = listOf(
                                FilterOptionConfig("most_relevant", "Most relevant"),
                                FilterOptionConfig("most_recent", "Most recent")
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "file_type",
                    label = "File type",
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "any",
                            options = listOf(
                                FilterOptionConfig("any", "Any"),
                                FilterOptionConfig("images", "Images"),
                                FilterOptionConfig("videos", "Videos"),
                                FilterOptionConfig("documents", "Documents"),
                                FilterOptionConfig("presentations", "Presentations"),
                                FilterOptionConfig("spreadsheets", "Spreadsheets"),
                                FilterOptionConfig("zoom_docs", "Zoom Docs")
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "file_in",
                    label = "In",
                    showSearchField = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, messageScopeOptions, "all"))
                ),
                FilterConfig(
                    id = "file_from",
                    label = "From",
                    showSearchField = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, peopleOptions, "anyone"))
                ),
                FilterConfig(
                    id = "file_date",
                    label = "Date",
                    showDone = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, commonDateOptions, "any_time"))
                )
            ),
            SearchCategory.Docs to listOf(
                FilterConfig(
                    id = "doc_owner",
                    label = "Owner",
                    showSearchField = true,
                    groups = listOf(FilterGroupConfig(SearchSheetGroupStyle.List, ownerOptions, "anyone"))
                ),
                FilterConfig(
                    id = "doc_date",
                    label = "Date",
                    showDone = true,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.Radio,
                            defaultOptionId = "last_modified",
                            options = listOf(
                                FilterOptionConfig("last_modified", "Last modified"),
                                FilterOptionConfig("created", "Created")
                            )
                        ),
                        FilterGroupConfig(SearchSheetGroupStyle.List, commonDateOptions, "any_time")
                    )
                ),
                FilterConfig(
                    id = "doc_scope",
                    label = "Scope",
                    displayMode = SearchFilterDisplayMode.AlwaysSelection,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "title_and_content",
                            options = listOf(
                                FilterOptionConfig("title_and_content", "Title and content"),
                                FilterOptionConfig("title_only", "Title only")
                            )
                        )
                    )
                )
            ),
            SearchCategory.Whiteboards to listOf(
                FilterConfig(
                    id = "whiteboard_sort",
                    label = "Type",
                    displayMode = SearchFilterDisplayMode.AlwaysSelection,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "modified_new_old",
                            options = listOf(
                                FilterOptionConfig("modified_new_old", "Modified (New-Old)"),
                                FilterOptionConfig("modified_old_new", "Modified (Old-New)"),
                                FilterOptionConfig("created_new_old", "Created (New-Old)"),
                                FilterOptionConfig("created_old_new", "Created (Old-New)"),
                                FilterOptionConfig("name_a_z", "Name (A-Z)"),
                                FilterOptionConfig("name_z_a", "Name (Z-A)")
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "whiteboard_owner",
                    label = "Owned by",
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "anyone",
                            options = listOf(
                                FilterOptionConfig("anyone", "Anyone"),
                                FilterOptionConfig("me", "Me"),
                                FilterOptionConfig("others", "Others")
                            )
                        )
                    )
                )
            ),
            SearchCategory.Mail to listOf(
                FilterConfig(
                    id = "mail_in",
                    label = "In",
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "all_mail",
                            options = listOf(FilterOptionConfig("all_mail", "All mail"))
                        )
                    )
                ),
                FilterConfig(
                    id = "mail_from",
                    label = "From",
                    showSearchField = true,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "anyone",
                            options = listOf(FilterOptionConfig("anyone", "Anyone", SearchSheetOptionLeading.Conversation))
                        )
                    )
                ),
                FilterConfig(
                    id = "mail_to",
                    label = "To",
                    showSearchField = true,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "anyone",
                            options = listOf(FilterOptionConfig("anyone", "Anyone", SearchSheetOptionLeading.Conversation))
                        )
                    )
                ),
                FilterConfig(
                    id = "mail_read_status",
                    label = "Read status",
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "all_mail",
                            options = listOf(
                                FilterOptionConfig("all_mail", "All mail"),
                                FilterOptionConfig("unread", "Unread")
                            )
                        )
                    )
                ),
                FilterConfig(
                    id = "mail_starred",
                    label = "Starred",
                    chipStyle = SearchFilterChipStyle.Toggle,
                    displayMode = SearchFilterDisplayMode.StaticLabel
                ),
                FilterConfig(
                    id = "mail_attachment",
                    label = "Has attachment",
                    chipStyle = SearchFilterChipStyle.Toggle,
                    displayMode = SearchFilterDisplayMode.StaticLabel
                ),
                FilterConfig(
                    id = "mail_date",
                    label = "Date",
                    showDone = true,
                    groups = listOf(
                        FilterGroupConfig(
                            style = SearchSheetGroupStyle.List,
                            defaultOptionId = "any_time",
                            options = listOf(
                                FilterOptionConfig("any_time", "Any time"),
                                FilterOptionConfig("today", "Today"),
                                FilterOptionConfig("last_7_days", "Last 7 days"),
                                FilterOptionConfig("last_30_days", "Last 30 days"),
                                FilterOptionConfig("this_year", "This year ($currentYear)"),
                                FilterOptionConfig("custom_range", "Custom range")
                            )
                        )
                    )
                )
            )
        )
    }
}
