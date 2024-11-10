package com.sli.radiostreamplayback

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentContainerView
import com.sli.radiostreamplayback.base.BaseActivity
import com.sli.radiostreamplayback.base.BaseErrorDialog
import com.sli.radiostreamplayback.databinding.ActivityMainBinding
import com.sli.radiostreamplayback.main.model.RadioStation
import com.sli.radiostreamplayback.playback.model.PlaybackStateHolder
import com.sli.radiostreamplayback.playback.model.ServiceAction
import com.sli.radiostreamplayback.playback.view.PlaybackFragment
import com.sli.radiostreamplayback.playback.view.PlaybackFragment.Companion.PLAYBACK_TAG
import com.sli.radiostreamplayback.playback.view.PlaybackFragment.Companion.RADIO_ITEM
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val fragmentContainerView: FragmentContainerView by lazy { binding.fragmentContainerView }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        checkForDeeplink(intent)
    }

    override fun onResume() {
        super.onResume()
        PlaybackStateHolder.isActivityAlive = true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkForDeeplink(intent)
    }

    private fun checkForDeeplink(intent: Intent) {
        if (intent.action == ServiceAction.OPEN.action && intent.extras != null) {

            val radioStation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.extras?.getSerializable(RADIO_ITEM, RadioStation::class.java)
            } else {
                intent.extras?.getSerializable(RADIO_ITEM)
            }

            if (radioStation == null || radioStation !is RadioStation) {
                return
            }

            supportFragmentManager.popBackStack()

            val fragment = PlaybackFragment()
            fragment.arguments = bundleOf(RADIO_ITEM to radioStation)

            navigateTo(fragment, PLAYBACK_TAG)
        } else if (intent.action == ServiceAction.ERROR.action) {
            val text = intent.extras?.getString(ServiceAction.ERROR.key)
            BaseErrorDialog.Builder()
                .setError(text)
                .setIsCancelable(true)
                .build()
                .show(supportFragmentManager, "ERROR")
        }
    }

    override fun onPause() {
        super.onPause()
        PlaybackStateHolder.isActivityAlive = false
    }
}