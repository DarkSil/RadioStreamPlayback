package com.sli.radiostreamplayback.main.presentation

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.sli.radiostreamplayback.main.model.SortResults
import com.sli.radiostreamplayback.main.model.SortType
import com.sli.radiostreamplayback.main.model.Tag
import com.sli.radiostreamplayback.main.model.TagsList

class SortViewModel : ViewModel() {

    companion object {
        const val TYPE_KEY = "TYPE_KEY"
        const val TAGS_KEY = "TAGS_KEY"
    }

    private var preSelectedType = 0

    private var selectedType = -1
    private var tags = TagsList(emptyList())

    fun preselectValues(arguments: Bundle?) {
        preSelectedType = arguments?.getInt(TYPE_KEY, 0) ?: 0

        val preselectedTags = arguments?.getSerializable(TAGS_KEY)
        if (preselectedTags != null && preselectedTags is TagsList) {
            tags = preselectedTags
        }
    }

    fun selectSortType(sortType: SortType) {
        selectedType = SortType.entries.indexOf(sortType)
    }

    fun proceedWithTag(tag: String, select: Boolean) {
        // Just to make sure we won't catch the bug if repeated value will pass here somehow
        findTags(tag).forEach {
            it.isSelected = select
        }
    }

    fun getSelectedType(): SortType {
        val position = if (selectedType == -1) {
            preSelectedType
        } else {
            selectedType
        }

        return SortType.entries[position]
    }

    fun getTags() : TagsList {
        return tags
    }

    fun getSelectedResults(): SortResults {
        val type = if (selectedType == -1) {
            preSelectedType
        } else {
            selectedType
        }
        return SortResults(SortType.entries[type], tags)
    }

    private fun findTags(tag: String): List<Tag> {
        return tags.tagsList.filter { it.tag == tag }
    }

}