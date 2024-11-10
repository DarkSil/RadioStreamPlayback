package com.sli.radiostreamplayback

import android.content.Context
import android.os.Bundle
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sli.radiostreamplayback.main.model.SortType
import com.sli.radiostreamplayback.main.model.Tag
import com.sli.radiostreamplayback.main.model.TagsList
import com.sli.radiostreamplayback.main.presentation.SortViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SortViewModelTest {

    @Test
    fun testPreselectValues() {
        val arguments = Bundle().apply {
            putInt(SortViewModel.TYPE_KEY, 1)
            putSerializable(SortViewModel.TAGS_KEY, TagsList(listOf(Tag("rock", true))))
        }

        val viewModel = SortViewModel()

        viewModel.preselectValues(arguments)

        val selectedType = viewModel.getSelectedType()
        val tags = viewModel.getTags()

        assertEquals(SortType.entries[1], selectedType)
        assertEquals(1, tags.tagsList.size)
        assertEquals("rock", tags.tagsList[0].tag)
        assertTrue(tags.tagsList[0].isSelected)
    }

    @Test
    fun testSelectSortType() {
        val viewModel = SortViewModel()

        viewModel.selectSortType(SortType.RELIABILITY_ASC)

        val selectedType = viewModel.getSelectedType()
        assertEquals(SortType.RELIABILITY_ASC, selectedType)
    }

    @Test
    fun testProceedWithTag() {
        val viewModel = SortViewModel()
        val tagsList = TagsList(listOf(Tag("rock", false), Tag("pop", false)))
        viewModel.preselectValues(Bundle().apply {
            putSerializable(SortViewModel.TAGS_KEY, tagsList)
        })

        viewModel.proceedWithTag("rock", true)

        val tags = viewModel.getTags()
        val rockTag = tags.tagsList.find { it.tag == "rock" }
        val popTag = tags.tagsList.find { it.tag == "pop" }

        assertNotNull(rockTag)
        assertTrue(rockTag!!.isSelected)

        assertNotNull(popTag)
        assertFalse(popTag!!.isSelected)
    }

    @Test
    fun testGetSelectedResults() {
        val viewModel = SortViewModel()
        val tagsList = TagsList(listOf(Tag("rock", true), Tag("pop", false)))
        viewModel.preselectValues(Bundle().apply {
            putSerializable(SortViewModel.TAGS_KEY, tagsList)
            putInt(SortViewModel.TYPE_KEY, 0)
        })

        viewModel.selectSortType(SortType.POPULARITY_DESC)
        viewModel.proceedWithTag("pop", true)

        val sortResults = viewModel.getSelectedResults()

        assertEquals(SortType.POPULARITY_DESC, sortResults.sortType)
        val selectedTags = sortResults.tags.tagsList.filter { it.isSelected }
        assertEquals(2, selectedTags.size)
        assertTrue(selectedTags.any { it.tag == "rock" })
        assertTrue(selectedTags.any { it.tag == "pop" })
    }
}