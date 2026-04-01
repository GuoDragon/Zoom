package com.example.zoom.presentation.search

import com.example.zoom.data.DataRepository
import com.example.zoom.model.Meeting
import com.example.zoom.model.Message
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SearchPresenter(private val view: SearchContract.View) : SearchContract.Presenter {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
    private val currentUser = DataRepository.getCurrentUser()
    private val currentUserInitial = currentUser.username
        .split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifBlank { "ME" }
    private val categoryFilters = SearchFilterCatalog.build(currentUserInitial)
    private val filterSelections = categoryFilters.mapValues { (_, filters) ->
        filters.associate { filter ->
            filter.id to FilterSelectionState(
                selectedOptionIds = filter.groups.map { it.defaultOptionId },
                enabled = false
            )
        }.toMutableMap()
    }.toMutableMap()

    private var selectedCategory = SearchCategory.TopResults
    private var currentQuery = ""
    private var activeSheetFilterId: String? = null
    private var activeSheetSearchQuery = ""

    override fun loadData() = publishState()

    override fun onQueryChanged(query: String) {
        currentQuery = query
        publishState()
    }

    override fun onCategorySelected(category: SearchCategory) {
        selectedCategory = category
        activeSheetFilterId = null
        activeSheetSearchQuery = ""
        publishState()
    }

    override fun onFilterChipClicked(filterId: String) {
        val filter = activeFilters().firstOrNull { it.id == filterId } ?: return
        if (filter.chipStyle == SearchFilterChipStyle.Toggle) {
            val selection = selectionFor(filterId)
            setSelection(filterId, selection.copy(enabled = !selection.enabled))
        } else {
            activeSheetFilterId = filterId
            activeSheetSearchQuery = ""
        }
        publishState()
    }

    override fun onSheetSearchChanged(query: String) {
        activeSheetSearchQuery = query
        publishState()
    }

    override fun onSheetOptionSelected(groupIndex: Int, optionId: String) {
        val filterId = activeSheetFilterId ?: return
        val filter = activeFilters().firstOrNull { it.id == filterId } ?: return
        val selectedIds = selectionFor(filterId).selectedOptionIds.toMutableList()
        while (selectedIds.size < filter.groups.size) {
            selectedIds += filter.groups[selectedIds.size].defaultOptionId
        }
        selectedIds[groupIndex] = optionId
        setSelection(filterId, selectionFor(filterId).copy(selectedOptionIds = selectedIds))
        if (!filter.showDone) {
            activeSheetFilterId = null
            activeSheetSearchQuery = ""
        }
        publishState()
    }

    override fun onSheetReset() {
        val filterId = activeSheetFilterId ?: return
        val filter = activeFilters().firstOrNull { it.id == filterId } ?: return
        setSelection(
            filterId,
            FilterSelectionState(
                selectedOptionIds = filter.groups.map { it.defaultOptionId },
                enabled = false
            )
        )
        activeSheetSearchQuery = ""
        publishState()
    }

    override fun onSheetDone() {
        activeSheetFilterId = null
        activeSheetSearchQuery = ""
        publishState()
    }

    override fun onSheetDismiss() = onSheetDone()

    private fun publishState() {
        val messageResults = buildMessageResults()
        val chatResults = buildChatResults()
        val meetingResults = buildMeetingResults()
        val contactResults = buildContactResults()

        view.showContent(
            SearchUiState(
                query = currentQuery,
                tabs = SearchCategory.entries,
                selectedCategory = selectedCategory,
                filters = buildFilterChips(),
                activeSheet = buildActiveSheet(),
                topResults = SearchTopResultsUiState(
                    messages = messageResults.take(3),
                    chats = chatResults.take(3),
                    meetings = meetingResults.take(3),
                    contacts = contactResults.take(3)
                ),
                messageResults = messageResults,
                chatResults = chatResults,
                meetingResults = meetingResults,
                contactResults = contactResults
            )
        )
    }

    private fun buildMessageResults(): List<MessageResult> {
        if (currentQuery.isBlank()) return emptyList()
        val inFilter = selectedOption(SearchCategory.Messages, "message_in")
        val fromFilter = selectedOption(SearchCategory.Messages, "message_from")
        val mentionFilter = selectedOption(SearchCategory.Messages, "message_mentions")
        val dateFilter = selectedOption(SearchCategory.Messages, "message_date")
        val sortFilter = selectedOption(SearchCategory.Messages, "message_sort")

        val results = DataRepository.searchMessages(currentQuery)
            .filter { message ->
                matchesMessageIn(message, inFilter) &&
                    matchesSender(message.senderId, fromFilter) &&
                    matchesMention(message, mentionFilter) &&
                    matchesDate(message.timestamp, dateFilter)
            }
            .let { list ->
                if (sortFilter == "most_recent") {
                    list.sortedByDescending { it.timestamp }
                } else {
                    list.sortedWith(compareByDescending<Message> { messageRelevance(it) }.thenByDescending { it.timestamp })
                }
            }

        return results.map { message ->
            MessageResult(
                messageId = message.messageId,
                meetingId = message.meetingId,
                senderId = message.senderId,
                senderName = message.senderName,
                senderInitial = message.senderName.firstOrNull()?.uppercase() ?: "?",
                meetingTopic = DataRepository.getMeetingById(message.meetingId)?.topic ?: message.meetingId,
                contentPreview = message.content,
                timeLabel = formatTimestamp(message.timestamp)
            )
        }
    }

    private fun buildChatResults(): List<ChatResult> {
        if (currentQuery.isBlank()) return emptyList()
        val managedBy = selectedOption(SearchCategory.Chats, "chat_managed_by")
        val type = selectedOption(SearchCategory.Chats, "chat_type")
        val sort = selectedOption(SearchCategory.Chats, "chat_sort")

        val results = DataRepository.searchChats(currentQuery)
            .filter { message ->
                val meeting = DataRepository.getMeetingById(message.meetingId)
                matchesManagedBy(meeting, managedBy) && matchesChatType(meeting, type)
            }
            .let { list ->
                when (sort) {
                    "most_recent" -> list.sortedByDescending { it.timestamp }
                    "fewest_members" -> list.sortedBy { DataRepository.getMeetingById(it.meetingId)?.participantIds?.size ?: 0 }
                    "a_to_z" -> list.sortedBy { DataRepository.getMeetingById(it.meetingId)?.topic ?: it.meetingId }
                    else -> list.sortedWith(compareByDescending<Message> { chatRelevance(it) }.thenByDescending { it.timestamp })
                }
            }

        return results.map { message ->
            ChatResult(
                meetingId = message.meetingId,
                chatName = DataRepository.getMeetingById(message.meetingId)?.topic ?: message.meetingId,
                lastMessage = message.content,
                timeLabel = formatTimestamp(message.timestamp)
            )
        }
    }

    private fun buildMeetingResults(): List<MeetingResult> {
        if (currentQuery.isBlank()) return emptyList()
        val date = selectedOption(SearchCategory.Meetings, "meeting_date")
        val type = selectedOption(SearchCategory.Meetings, "meeting_type")

        return DataRepository.searchMeetings(currentQuery)
            .filter { meeting -> matchesDate(meeting.startTime, date) && matchesMeetingType(type) }
            .sortedByDescending { it.startTime }
            .map { meeting ->
                MeetingResult(
                    meetingId = meeting.meetingId,
                    topic = meeting.topic,
                    dateTimeLabel = "${dateFormat.format(Date(meeting.startTime))} ${timeFormat.format(Date(meeting.startTime))}",
                    participantCount = meeting.participantIds.size
                )
            }
    }

    private fun buildContactResults(): List<ContactResult> {
        if (currentQuery.isBlank()) return emptyList()
        return DataRepository.searchUsers(currentQuery)
            .sortedBy { it.username }
            .map { user ->
                ContactResult(
                    userId = user.userId,
                    username = user.username,
                    email = user.email ?: "",
                    initial = user.username.firstOrNull()?.uppercase() ?: "?"
                )
            }
    }

    private fun buildFilterChips(): List<SearchFilterChipUiState> {
        return activeFilters().map { filter ->
            val selection = selectionFor(filter.id)
            SearchFilterChipUiState(
                id = filter.id,
                label = chipLabel(filter, selection),
                selected = if (filter.chipStyle == SearchFilterChipStyle.Toggle) {
                    selection.enabled
                } else {
                    filter.groups.indices.any { index ->
                        selection.selectedOptionIds.getOrNull(index) != filter.groups[index].defaultOptionId
                    }
                },
                style = filter.chipStyle
            )
        }
    }

    private fun buildActiveSheet(): SearchFilterSheetUiState? {
        val filterId = activeSheetFilterId ?: return null
        val filter = activeFilters().firstOrNull { it.id == filterId } ?: return null
        val selection = selectionFor(filterId)

        return SearchFilterSheetUiState(
            title = filter.label,
            showDone = filter.showDone,
            showSearchField = filter.showSearchField,
            searchQuery = activeSheetSearchQuery,
            groups = filter.groups.mapIndexed { index, group ->
                SearchSheetGroupUiState(
                    style = group.style,
                    options = group.options
                        .filter { option ->
                            !filter.showSearchField ||
                                activeSheetSearchQuery.isBlank() ||
                                option.label.contains(activeSheetSearchQuery, ignoreCase = true)
                        }
                        .map { option ->
                            SearchSheetOptionUiState(
                                id = option.id,
                                label = option.label,
                                selected = selection.selectedOptionIds.getOrNull(index) == option.id,
                                leading = option.leading
                            )
                        }
                )
            }
        )
    }

    private fun chipLabel(filter: FilterConfig, selection: FilterSelectionState): String {
        if (filter.chipStyle == SearchFilterChipStyle.Toggle) return filter.label
        val selectedId = selection.selectedOptionIds.firstOrNull()
        val selectedLabel = filter.groups.firstOrNull()?.options?.firstOrNull { it.id == selectedId }?.label ?: filter.label
        return when (filter.displayMode) {
            SearchFilterDisplayMode.LabelWhenDefault -> {
                val defaultId = filter.groups.firstOrNull()?.defaultOptionId
                if (selectedId == defaultId) filter.label else selectedLabel
            }
            SearchFilterDisplayMode.AlwaysSelection -> selectedLabel
            SearchFilterDisplayMode.StaticLabel -> filter.label
        }
    }

    private fun activeFilters(): List<FilterConfig> = categoryFilters[selectedCategory].orEmpty()

    private fun selectionFor(filterId: String): FilterSelectionState {
        return filterSelections[selectedCategory]?.get(filterId) ?: FilterSelectionState()
    }

    private fun setSelection(filterId: String, state: FilterSelectionState) {
        filterSelections[selectedCategory]?.set(filterId, state)
    }

    private fun selectedOption(category: SearchCategory, filterId: String): String {
        return filterSelections[category]?.get(filterId)?.selectedOptionIds?.firstOrNull().orEmpty()
    }

    private fun messageRelevance(message: Message): Int {
        val query = currentQuery.lowercase()
        return when {
            message.senderName.lowercase().contains(query) -> 3
            message.content.lowercase().startsWith(query) -> 2
            message.content.lowercase().contains(query) -> 1
            else -> 0
        }
    }

    private fun chatRelevance(message: Message): Int {
        val topic = DataRepository.getMeetingById(message.meetingId)?.topic.orEmpty()
        val query = currentQuery.lowercase()
        return if (topic.lowercase().contains(query)) 2 else if (message.content.lowercase().contains(query)) 1 else 0
    }

    private fun matchesMessageIn(message: Message, filterId: String): Boolean = when (filterId) {
        "bookmarked" -> false
        "my_space" -> message.senderId == currentUser.userId
        else -> true
    }

    private fun matchesSender(senderId: String, filterId: String): Boolean = when (filterId) {
        "me" -> senderId == currentUser.userId
        else -> true
    }

    private fun matchesMention(message: Message, filterId: String): Boolean = when (filterId) {
        "me" -> message.content.lowercase().contains("@${currentUser.username.substringBefore(" ").lowercase()}")
        else -> true
    }

    private fun matchesManagedBy(meeting: Meeting?, filterId: String): Boolean = when {
        meeting == null -> false
        filterId == "me" -> currentUser.userId in meeting.participantIds
        else -> true
    }

    private fun matchesChatType(meeting: Meeting?, filterId: String): Boolean = when {
        meeting == null -> false
        filterId == "channels" -> meeting.participantIds.size >= 8
        filterId == "group_chats" -> meeting.participantIds.size in 4..7
        filterId == "direct_messages" -> meeting.participantIds.size <= 3
        else -> true
    }

    private fun matchesMeetingType(filterId: String): Boolean = filterId != "recurring"

    private fun matchesDate(timestamp: Long, filterId: String): Boolean {
        if (filterId.isBlank() || filterId == "any_time") return true
        val now = Calendar.getInstance()
        val sample = Calendar.getInstance().apply { timeInMillis = timestamp }
        val days = ((now.timeInMillis - sample.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
        return when (filterId) {
            "today" -> sameDay(now, sample)
            "yesterday" -> days == 1
            "last_7_days" -> days in 0..6
            "last_30_days" -> days in 0..29
            "this_year" -> now.get(Calendar.YEAR) == sample.get(Calendar.YEAR)
            "custom_range" -> false
            else -> true
        }
    }

    private fun sameDay(first: Calendar, second: Calendar): Boolean {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
            first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val now = Calendar.getInstance()
        val sample = Calendar.getInstance().apply { timeInMillis = timestamp }
        return if (sameDay(now, sample)) timeFormat.format(Date(timestamp)) else dateFormat.format(Date(timestamp))
    }
}
