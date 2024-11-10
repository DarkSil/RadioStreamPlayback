package com.sli.radiostreamplayback.playback.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.sli.radiostreamplayback.R
import com.sli.radiostreamplayback.databinding.PlaybackFragmentBinding
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.playback.presentation.PlaybackViewModel

class PlaybackFragment : Fragment() {

    companion object {
        const val PLAYBACK_TAG = "PLAYBACK"
        const val RADIO_ITEM = "RADIO_ITEM"
    }

    private val binding by lazy { PlaybackFragmentBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(this)[PlaybackViewModel::class] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val radioStation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(RADIO_ITEM, RadioStation::class.java)
        } else {
            arguments?.getSerializable(RADIO_ITEM)
        }

        if (radioStation != null && radioStation is RadioStation) {
            prepareAudioStatus(radioStation)
            setupUI(radioStation)
        }
    }

    private fun prepareAudioStatus(radioStation: RadioStation) {
        viewModel.getStationPlaybackStatus(radioStation).observe(viewLifecycleOwner) { isPlaying ->
            val image = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            binding.imagePlayPause.setImageResource(image)
        }
    }

    private fun setupUI(radioStation: RadioStation) {
        binding.textName.text = radioStation.name
        binding.textDescription.text = radioStation.description

        Glide.with(requireContext())
            .load(radioStation.imgUrl)
            .into(binding.imageLogo)

        binding.imagePlayPause.setOnClickListener {
            viewModel.playPause(requireContext())
        }
    }
}