package com.example.streamingvideoapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.streamingvideoapp.R
import com.example.streamingvideoapp.databinding.ActivitySplashScreenBinding

class SplashScreenActivity:AppCompatActivity() {
    lateinit var binding:ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        binding.nextBtn.setOnClickListener {
            val intent=Intent(this, StreamingVideoActivity::class.java)
            startActivity(intent)
        }

    }
}