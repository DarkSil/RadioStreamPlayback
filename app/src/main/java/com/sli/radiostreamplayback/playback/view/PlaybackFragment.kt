package com.sli.radiostreamplayback.playback.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sli.radiostreamplayback.databinding.PlaybackFragmentBinding
import com.sli.radiostreamplayback.playback.model.RadioService

class PlaybackFragment : Fragment() {

    companion object {
        const val PLAYBACK_TAG = "PLAYBACK"
        const val RADIO_ITEM = "RADIO_ITEM"
    }

    private val binding by lazy { PlaybackFragmentBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val intent = Intent(context, RadioService::class.java).apply {
            this.action = RadioService.ACTION_PLAY
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(intent)
        } else{
            requireActivity().startService(intent)
        }
    }
}