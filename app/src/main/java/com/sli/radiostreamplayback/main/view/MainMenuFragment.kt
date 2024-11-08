package com.sli.radiostreamplayback.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sli.radiostreamplayback.databinding.MainMenuFragmentBinding
import com.sli.radiostreamplayback.main.presentation.MainMenuViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuFragment : Fragment() {

    private val binding by lazy { MainMenuFragmentBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[MainMenuViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getListOfStations().observe(viewLifecycleOwner) { list ->
            list.radioList?.let {

            }

            list.error?.let {

            }

            list?.progress?.let {

            }
        }
    }

}