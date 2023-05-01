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
import android.view.KeyEvent
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.streamingvideoapp.R
import com.example.streamingvideoapp.databinding.ActivityCitySearchBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class SearchCityActivity:AppCompatActivity() {

lateinit var binding:ActivityCitySearchBinding
    var APP_ID = "468c91e76ff3f2501647e15f63bdf150"
    val WEATHER_URL = "https://api.openweathermap.org/data/3.0/weather"
    var Location_Provider = LocationManager.GPS_PROVIDER
    var mLocationManager: LocationManager? = null
    var mLocationListner: LocationListener? = null
    val MIN_TIME: Long = 5000
    val MIN_DISTANCE = 1000f
    val REQUEST_CODE = 101

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      binding=DataBindingUtil.setContentView(this, R.layout.activity_city_search)

        binding.backButton.setOnClickListener {
            val intent = Intent(this@SearchCityActivity, StreamingVideoActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.currentDay.setText(getCurrentDate())
        binding.searchCity.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                val newCity: String = binding.searchCity.getText().toString()
                val intent = Intent(this@SearchCityActivity, SearchCityActivity::class.java)
                intent.putExtra("City", newCity)
                startActivity(intent)

                return false
            }

        })


    }
    fun letsdoSomeNetworking(params: RequestParams) {
        val client = AsyncHttpClient()
        client[WEATHER_URL, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header?>?,
                response: JSONObject?
            ) {
                Toast.makeText(this@SearchCityActivity, "Data Get Success", Toast.LENGTH_SHORT).show()

                val weatherD: WeatherData? = WeatherData().fromJson(response!!)
                updateUI(weatherD!!)

            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header?>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                Toast.makeText(this@SearchCityActivity, "Data not Success", Toast.LENGTH_SHORT).show()
            }
        }]
    }
    fun getWeatherForNewCity(city: String) {
        val params = RequestParams()
        params.put("q", city)
        params.put("appid", APP_ID)
        letsdoSomeNetworking(params)
    }
    @SuppressLint("SetTextI18n", "DiscouragedApi")
    fun updateUI(weather: WeatherData) {

        binding.cityName.text = weather.getmTemperature() +" "+ resources.getString(R.string.in_)+ " "+ weather.getMcity()

        binding.temperature.text = resources.getString(R.string.feels_like) + " " + weather.getmWeatherType() + " " + resources.getString(
            R.string.forecast
        )

        binding.humidityCount.setText(weather.getmhumidity())
        binding.pressureCount.setText(weather.getPressure())

        binding.speedCount.setText(weather.getwindSpeed())
        val resourceID:Int = resources.getIdentifier(
            weather.getMicon(), "drawable",
            packageName
        )
        binding.weatherIcon.setImageResource(resourceID)
    }

    private fun getWeatherForCurrentLocation() {
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListner = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val Latitude: String = java.lang.String.valueOf(location.getLatitude())
                val Longitude: String = java.lang.String.valueOf(location.getLongitude())
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
            }
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_CODE)
            return
        }
        mLocationManager!!.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner!!)

    }

    override fun onResume() {
        super.onResume()
        val mIntent: Intent = intent
        val city: String? = mIntent.getStringExtra("City")
        if(city!=null){
            getWeatherForNewCity(city)
        }
        else{
            getWeatherForCurrentLocation()
        }
    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@SearchCityActivity, StreamingVideoActivity::class.java)
        startActivity(intent)
        finish()
    }

}