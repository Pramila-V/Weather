package com.example.streamingvideoapp.activity.model

import com.google.gson.annotations.SerializedName

class MediaFilesModel(
    @SerializedName("id")
    val id: Int,
    var title: String,
    var displayName: String,
    var size: String,
    var duration: String,
    var path: String,
    var url: String
)

/*data class MediaFilesModel(val displayName: Int, val duration: String, var path:String) {
}*/

