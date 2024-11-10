package com.sli.radiostreamplayback.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.sli.radiostreamplayback.R
import com.sli.radiostreamplayback.databinding.SortFragmentBinding
import com.sli.radiostreamplayback.main.model.SortResults
import com.sli.radiostreamplayback.main.model.SortType
import com.sli.radiostreamplayback.main.presentation.SortViewModel

class SortFragment : BottomSheetDialogFragment() {

    fun interface SelectedListener {
        fun OnSelected(sortResults: SortResults)
    }

    private val binding by lazy { SortFragmentBinding.inflate(layoutInflater) }
    private var listener: SelectedListener? = null

    private val viewModel by lazy { ViewModelProvider(this)[SortViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.preselectValues(arguments)

        binding.imageClose.setOnClickListener {
            dismiss()
        }

        binding.buttonApply.setOnClickListener {
            listener?.OnSelected(viewModel.getSelectedResults())
            dismiss()
        }

        setupSortItems()
        setupTags()
    }

    private fun setupSortItems() {
        val selectedType = viewModel.getSelectedType()
        SortType.entries.forEach {
            val chip = layoutInflater.inflate(
                R.layout.sort_chip_placeholder,
                binding.sortChipGroup,
                false
            ) as Chip

            if (it == selectedType) {
                chip.isChecked = true
            }

            chip.text = requireContext().getString(it.stringId)
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.selectSortType(it)
                }
            }

            binding.sortChipGroup.addView(chip)
        }
    }

    private fun setupTags() {
        val tagsList = viewModel.getTags().tagsList
        tagsList.forEach {
            val chip = layoutInflater.inflate(
                R.layout.sort_chip_placeholder,
                binding.sortChipGroup,
                false
            ) as Chip

            if (it.isSelected) {
                chip.isChecked = true
            }

            chip.text = it.tag
            chip.setOnCheckedChangeListener { _, isChecked ->
                viewModel.proceedWithTag(it.tag, isChecked)
            }

            binding.filterChipGroup.addView(chip)
        }
    }

    fun addSelectedListener(listener: SelectedListener) : SortFragment {
        this.listener = listener
        return this
    }
}