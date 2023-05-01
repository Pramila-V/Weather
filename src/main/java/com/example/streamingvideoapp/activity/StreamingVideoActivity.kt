package com.example.streamingvideoapp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.streamingvideoapp.R
import com.example.streamingvideoapp.activity.Adapter.VideoViewAdapter
import com.example.streamingvideoapp.activity.model.MediaFilesModel
import com.example.streamingvideoapp.databinding.ActivityStreamingVideoBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.toolbar.view.*
import org.json.JSONObject


class StreamingVideoActivity : AppCompatActivity() {

    lateinit var binding: ActivityStreamingVideoBinding

    val WEATHER_URL = "https://api.openweathermap.org/data/3.0/weather"
    val MIN_TIME: Long = 5000
    val MIN_DISTANCE = 1000f
    val REQUEST_CODE = 101

    var APP_ID = "468c91e76ff3f2501647e15f63bdf150"
    var context: Context? = null
    var Location_Provider = LocationManager.GPS_PROVIDER
    var mLocationManager: LocationManager? = null
    var mLocationListner: LocationListener? = null
    var videoList = ArrayList<MediaFilesModel>()


    companion object {
        lateinit var videoViewAdapter: VideoViewAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_streaming_video)

        context = this
        getWeatherForCurrentLocation()

        binding.videoRecyclerview.layoutManager = LinearLayoutManager(this)


        for (i in 1..10) {

            videoList.add(
                MediaFilesModel(
                    0,
                    "Big buck video",
                    "Big buck video",
                    "",
                    "01:40",
                    "",
                    "https://www.rmp-streaming.com/media/big-buck-bunny-360p.mp4"
                )
            )

        }
        videoViewAdapter = VideoViewAdapter(videoList)
        binding.videoRecyclerview.visibility = View.VISIBLE
        binding.videoRecyclerview.setHasFixedSize(true)

        binding.videoRecyclerview.adapter = videoViewAdapter

        binding.linear.setOnClickListener {
            val intent = Intent(this@StreamingVideoActivity, SearchCityActivity::class.java)
            startActivity(intent)
        }
        animRocket()

        binding.toolbarView.title.text = "Streaming Videos"


    }

    override fun onResume() {
        super.onResume()
        getWeatherForCurrentLocation()
    }

    private fun animRocket() {
        val anim = AnimationUtils.loadAnimation(binding.root.context, R.anim.hand_anim)
        binding.imgRightArrw.animation = anim
    }

    private fun getWeatherForCurrentLocation() {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListner = object : LocationListener {

            override fun onLocationChanged(location: Location) {
                val Latitude: String = java.lang.String.valueOf(location.latitude)
                val Longitude: String = java.lang.String.valueOf(location.longitude)
                val params = RequestParams()
                params.put("lat", Latitude)
                params.put("lon", Longitude)
                params.put("appid", APP_ID)
                letsdoSomeNetworking(params)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                //not able to get location
                Toast.makeText(
                    this@StreamingVideoActivity,
                    "Not able to get loaction",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE
            )
            return
        }
        mLocationManager!!.requestLocationUpdates(
            Location_Provider,
            MIN_TIME,
            MIN_DISTANCE,
            mLocationListner!!
        )


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@StreamingVideoActivity,
                    "Location get Succesffully",
                    Toast.LENGTH_SHORT
                )
                    .show()
                getWeatherForCurrentLocation()

            }
        }
    }

    fun letsdoSomeNetworking(params: RequestParams) {
        val client = AsyncHttpClient()
        client[WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header?>?,
                response: JSONObject?
            ) {
                Toast.makeText(this@StreamingVideoActivity, "Data Get Success", Toast.LENGTH_SHORT)
                    .show()


                val weatherD: WeatherData? = WeatherData().fromJson(response!!)
                updateUI(weatherD!!)


            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header?>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                Toast.makeText(this@StreamingVideoActivity, "Data not Success", Toast.LENGTH_SHORT)
                    .show()
            }
        }]
    }

    @SuppressLint("SetTextI18n")
    fun updateUI(weather: WeatherData) {

        binding.cityName.text = weather.getmTemperature() + " " + resources.getString(R.string.in_) + " " + weather.getMcity()
        binding.temperature.text = resources.getString(R.string.feels_like) + " " + weather.getmWeatherType() + " " + resources.getString(
            R.string.forecast
        )

    }

    override fun onPause() {
        super.onPause()
        if (mLocationManager != null) {
            mLocationManager!!.removeUpdates(mLocationListner!!)
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val i = Intent(binding.root.context, SplashScreenActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(i)
    }

}