package com.example.streamingvideoapp.activity.Adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.streamingvideoapp.R
import com.example.streamingvideoapp.activity.ExpandVideoActivity
import com.example.streamingvideoapp.activity.model.MediaFilesModel
import com.example.streamingvideoapp.databinding.VideoViewItemBinding


class VideoViewAdapter(private var videoList: ArrayList<MediaFilesModel>) : RecyclerView.Adapter<VideoViewAdapter.ViewHolder>() {

    lateinit var binding: VideoViewItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.video_view_item,
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {

        holder.binding.videoName.text=videoList.get(position).displayName
        holder.binding.videoDuration.text=videoList.get(position).duration


        holder.binding.menuMore.setOnClickListener {
            Toast.makeText(binding.root.context, "menu more", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(binding.root.context, ExpandVideoActivity::class.java)
            binding.root.context.startActivity(intent)
        }
    }

      override fun getItemId(position: Int): Long {
          return position.toLong()
      }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    inner class ViewHolder(var binding: VideoViewItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )


    fun timeConversion(value: String): String? {
        val videoTime: String
        val duration = value.toInt()
        val hrs = duration / 3600000
        val mns = duration / 60000 % 60000
        val scs = duration % 60000 / 10000
        videoTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }

        return videoTime
    }
}