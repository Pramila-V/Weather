package com.example.streamingvideoapp.activity

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.streamingvideoapp.R
import com.example.streamingvideoapp.databinding.ActivityExpandVideoBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.android.synthetic.main.activity_controller.view.*


class ExpandVideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityExpandVideoBinding
    lateinit var handler: Handler
    lateinit var simpleExoPlayer: SimpleExoPlayer

    companion object {
        var isFullScreen = false
        var isLock = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expand_video)
        handler = Handler(Looper.getMainLooper())



        binding.player.bt_fullscreen.setOnClickListener {

            if (!isFullScreen) {
                binding.player.bt_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_fullscreen_exit
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                binding.player.bt_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_fullscreen
                    )
                )
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            isFullScreen = !isFullScreen
        }

        binding.player.exo_lock.setOnClickListener {
            if (!isLock) {
                binding.player.exo_lock.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_lock
                    )
                )
            } else {
                binding.player.exo_lock.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.ic_baseline_lock_open
                    )
                )
            }
            isLock = !isLock
            lockScreen(isLock)
        }

        simpleExoPlayer = SimpleExoPlayer.Builder(this)
            .setSeekBackIncrementMs(5000)
            .setSeekForwardIncrementMs(5000)
            .build()
        binding.player.player = simpleExoPlayer
        binding.player.keepScreenOn = true
        simpleExoPlayer.addListener(object : Player.Listener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                if (playbackState == Player.STATE_BUFFERING) {
                    binding.progressBar.visibility = View.VISIBLE
                } else if (playbackState == Player.STATE_READY) {
                    binding.progressBar.visibility = View.GONE
                }

                if (!simpleExoPlayer.playWhenReady) {
                    handler.removeCallbacks(updateProgressAction)
                } else {
                    onProgress()
                }
            }
        })

        val videoSource = Uri.parse("https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4")

        val mediaItem = MediaItem.fromUri(videoSource)
        simpleExoPlayer.setMediaItem(mediaItem)
        simpleExoPlayer.prepare()
        simpleExoPlayer.play()


    }

    //at 4 second
    val ad = 4000
    var check = false

    fun onProgress() {
        val player = simpleExoPlayer
        val position: Long = if (player == null) 0 else player.currentPosition

        handler.removeCallbacks(updateProgressAction)

        val playbackState = if (player == null) Player.STATE_IDLE else player.playbackState

        if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
            var delayMs: Long

            if (player.playWhenReady && playbackState == Player.STATE_READY) {
                delayMs = 1000 - position % 1000
                if (delayMs < 200) {
                    delayMs += 1000
                }
            } else {
                delayMs = 1000
            }

            //check to display ad
            if ((ad - 3000 <= position && position <= ad) && !check) {
                check = true

            }
            handler.postDelayed(updateProgressAction, delayMs)
        }
    }


    private val updateProgressAction = Runnable { onProgress() }

    private fun lockScreen(lock: Boolean) {
        val sec_mid = findViewById<LinearLayout>(R.id.sec_controlvid1)
        val sec_bottom = findViewById<LinearLayout>(R.id.sec_controlvid2)
        if (lock) {
            sec_mid.visibility = View.INVISIBLE
            sec_bottom.visibility = View.INVISIBLE
        } else {
            sec_mid.visibility = View.VISIBLE
            sec_bottom.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (isLock) return
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.player.bt_fullscreen.performClick()
        } else super.onBackPressed()

    }

    override fun onStop() {
        super.onStop()
        simpleExoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleExoPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer.pause()
    }
}