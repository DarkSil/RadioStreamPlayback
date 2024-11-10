package com.sli.radiostreamplayback.playback.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.sli.radiostreamplayback.MainActivity
import com.sli.radiostreamplayback.R

class RadioService : Service() {

    // I did not worked with ExoPlayer for a bit and faced some issues with playback for Media3
    // so I used the old v2 library to make it work smoothly
    private var exoPlayer: ExoPlayer? = null
    private var mediaSession: MediaSessionCompat? = null
    private var audioManager: AudioManager? = null

    companion object {
        const val ACTION_PLAY = "com.sli.radiostreamplayback.ACTION_PLAY"
        const val ACTION_PAUSE = "com.sli.radiostreamplayback.ACTION_PAUSE"
        const val ACTION_STOP = "com.sli.radiostreamplayback.ACTION_STOP"
        const val CHANNEL_ID = "RadioChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        initializeMediaSession()
        createNotificationChannel()
        startForegroundService()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        exoPlayer?.addListener(playerListener)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Radio Stream Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Radio Stream Playback channel to manage it's notifications"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initializeMediaSession() {
        mediaSession = MediaSessionCompat(this, "RadioService")
        mediaSession?.setCallback(mediaSessionCallback)
        mediaSession?.isActive = true
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    // Still using custom images and actions to support Android 12 and lower version devices
    private fun getPlayPauseAction() : NotificationCompat.Action {
        return if (exoPlayer?.isPlaying == true) {
            notificationAction(R.drawable.ic_pause, "Pause", ACTION_PAUSE)
        } else {
            notificationAction(R.drawable.ic_play, "Play", ACTION_PLAY)
        }
    }

    private fun createNotification(): Notification {
        val playPauseAction = getPlayPauseAction()

        val closeAction = notificationAction(R.drawable.ic_close, "Stop", ACTION_STOP)

        //TODO Add station name and image
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Radio Stream Playback")
            .setContentText("Playing STATION")
            .setSmallIcon(R.drawable.ic_radio)
            .setContentIntent(getActivityPendingIntent())
            .setDeleteIntent(getNotificationDeletedIntent())
            .addAction(playPauseAction)
            .addAction(closeAction)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession?.sessionToken)
                    .setShowActionsInCompactView(0, 1)
            )
            .setOngoing(true)

        return notificationBuilder.build()
    }

    private fun notificationAction(iconResId: Int, title: String, action: String): NotificationCompat.Action {
        val intent = Intent(this, NotificationActionReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action(iconResId, title, pendingIntent)
    }

    private fun getNotificationDeletedIntent(): PendingIntent {
        val intent = Intent(this, NotificationActionReceiver::class.java).apply {
            this.action = ACTION_STOP
        }
        return PendingIntent.getBroadcast(
            this,
            ACTION_STOP.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mediaButtonIntent = intent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (mediaButtonIntent != null) {
            if (exoPlayer?.isPlaying == false) {
                mediaSession?.controller?.transportControls?.play()
            } else {
                mediaSession?.controller?.transportControls?.pause()
            }
        } else {
            handleIntent(intent)
        }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_PLAY -> playStream()
            ACTION_PAUSE -> pauseStream()
            ACTION_STOP -> stopPlayback()
            else -> playStream()
        }
    }

    private fun stopPlayback() {
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun playStream() {
        val result = audioManager?.requestAudioFocus(audioFocusChangeListener,
            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // TODO Set link programmatically
            val mediaItem = MediaItem.fromUri("https://c2.prostream365.com:8000/live")
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
            updateNotification()
        }
    }

    private fun pauseStream() {
        exoPlayer?.pause()
        updateNotification()
    }

    private fun updateNotification() {
        val notification = createNotification()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getAvailableActions(isPlaying: Boolean): Long {
        var actions = PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE
        actions = actions or PlaybackStateCompat.ACTION_STOP

        actions = if (isPlaying) {
            actions or PlaybackStateCompat.ACTION_PAUSE
        } else {
            actions or PlaybackStateCompat.ACTION_PLAY
        }

        return actions
    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> pauseStream()
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {

            val playState = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
            mediaSession?.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(playState, exoPlayer?.contentPosition ?: -1L, 1f)
                    .setActions(getAvailableActions(isPlaying))
                    .build()
            )
            updateNotification()
        }
    }

    // TODO Add some variable to handle App's UI
    private val mediaSessionCallback = object : MediaSessionCompat.Callback() {
        override fun onPlay() {
            playStream()
        }

        override fun onPause() {
            pauseStream()
        }

        override fun onStop() {
            stopPlayback()
        }
    }

    override fun onDestroy() {
        exoPlayer?.release()
        mediaSession?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}