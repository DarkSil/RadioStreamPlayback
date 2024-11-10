package com.sli.radiostreamplayback.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sli.radiostreamplayback.R
import com.sli.radiostreamplayback.base.BaseActivity
import com.sli.radiostreamplayback.base.BaseErrorDialog
import com.sli.radiostreamplayback.base.Reason
import com.sli.radiostreamplayback.databinding.MainMenuFragmentBinding
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.main.presentation.MainMenuViewModel
import com.sli.radiostreamplayback.main.presentation.SortViewModel.Companion.TAGS_KEY
import com.sli.radiostreamplayback.main.presentation.SortViewModel.Companion.TYPE_KEY
import com.sli.radiostreamplayback.playback.view.PlaybackFragment
import com.sli.radiostreamplayback.playback.view.PlaybackFragment.Companion.PLAYBACK_TAG
import com.sli.radiostreamplayback.playback.view.PlaybackFragment.Companion.RADIO_ITEM
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
        setupStatusBar()

        viewModel.getListOfStations().observe(viewLifecycleOwner) { state ->
            binding.linearProgress.isVisible = state.progress

            state.radioList?.let {
                adapter.setList(it.radioList)
            }

            state.error?.let {
                val label = when (it) {
                    Reason.Internet -> getString(R.string.internet_error)
                    Reason.Unknown -> null
                    is Reason.Specified -> null
                }

                BaseErrorDialog.Builder()
                    .setLabel(label)
                    .setError(it.reason)
                    .setButtonText(getString(R.string.try_again))
                    .setIsCancelable(false)
                    .setButtonListener { dialog ->
                        viewModel.reloadListOfStations()
                        dialog.dismiss()
                    }
                    .build()
                    .show(parentFragmentManager, "ERROR")
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
            navigateToPlayback(station)
        }
        adapter.setListenClickListener { station ->
            viewModel.performStationPlay(station, requireContext())
        }

        binding.imageSort.setOnClickListener {
            val fragment = SortFragment()
            fragment.arguments = bundleOf(
                TYPE_KEY to viewModel.selectedSortType.ordinal,
                TAGS_KEY to viewModel.tagsList
            )
            fragment
                .addSelectedListener { sortResults ->
                    adapter.setList(viewModel.getUpdatedList(sortResults))
                }
                .show(parentFragmentManager, "SORT")
        }

        binding.linearProgress.setOnClickListener {}
    }

    private fun setupStatusBar() {
        val status = viewModel.getLastStationStatus()
        val statusIsPlaying = status.value?.isPlaying == true
        binding.linearCurrentPlayingState.isVisible = statusIsPlaying

        binding.playPauseButton.setOnClickListener {
            status.value?.radioStation?.let {
                viewModel.performPlayPause(it, requireContext())
            }
        }

        binding.linearCurrentPlayingState.setOnClickListener {
            status.value?.radioStation?.let {
                navigateToPlayback(it)
            }
        }

        status.observe(viewLifecycleOwner) { lastStationStatus ->
            binding.linearCurrentPlayingState.isVisible = lastStationStatus?.radioStation != null
            val isPlaying = lastStationStatus?.isPlaying
            val imageRes = if (isPlaying != true) R.drawable.ic_play else R.drawable.ic_pause
            binding.playPauseButton.setImageResource(imageRes)

            lastStationStatus?.radioStation?.let { radioStation ->
                binding.textCurrentPlayingName.text = radioStation.name
                Glide.with(requireContext())
                    .load(radioStation.imgUrl)
                    .into(binding.currentPlayingImage)
            }
        }
    }

    private fun navigateToPlayback(station: RadioStation) {
        val fragment = PlaybackFragment()
        fragment.arguments = bundleOf(RADIO_ITEM to station)
        (requireActivity() as BaseActivity).navigateTo(fragment, PLAYBACK_TAG)
    }

}