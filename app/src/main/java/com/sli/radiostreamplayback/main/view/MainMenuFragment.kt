package com.sli.radiostreamplayback.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sli.radiostreamplayback.databinding.MainMenuFragmentBinding
import com.sli.radiostreamplayback.main.presentation.MainMenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private val binding by lazy { MainMenuFragmentBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[MainMenuViewModel::class.java] }
    private val adapter = StationsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayout()

        viewModel.getListOfStations().observe(viewLifecycleOwner) { state ->
            binding.linearProgress.isVisible = state.progress

            state.radioList?.let {
                adapter.setList(it.radioList)
            }

            state.error?.let {
                // TODO Show a specified error
            }
        }
    }

    private fun setupLayout() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        adapter.setOnItemClickListener { station ->
            Toast.makeText(requireContext(), station.name, Toast.LENGTH_LONG).show()
            // TODO Navigate further with item
        }
    }

}