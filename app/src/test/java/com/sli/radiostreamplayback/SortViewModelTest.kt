package com.sli.radiostreamplayback

import android.os.Bundle
import com.sli.radiostreamplayback.main.model.SortType
import com.sli.radiostreamplayback.main.model.Tag
import com.sli.radiostreamplayback.main.model.TagsList
import com.sli.radiostreamplayback.main.presentation.SortViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

// Same as androidTest JUnit realization but running on JVM so no need to run emulator
@RunWith(RobolectricTestRunner::class)
class SortViewModelTest {

    private lateinit var viewModel: SortViewModel

    @Before
    fun setUp() {
        viewModel = SortViewModel()
    }

    @Test
    fun testPreselectValues() {
        val arguments = Bundle().apply {
            putInt(SortViewModel.TYPE_KEY, 1)
            putSerializable(SortViewModel.TAGS_KEY, TagsList(listOf(Tag("rock", true))))
        }

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
        viewModel.selectSortType(SortType.RELIABILITY_ASC)

        val selectedType = viewModel.getSelectedType()
        assertEquals(SortType.RELIABILITY_ASC, selectedType)
    }

    @Test
    fun testProceedWithTag() {
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