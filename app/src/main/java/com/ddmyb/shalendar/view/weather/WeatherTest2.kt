package com.ddmyb.shalendar.view.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityWeatherTestBinding
import com.ddmyb.shalendar.view.schedules.utils.Permission.Companion.REQUIRED_PERMISSIONS
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDate

class WeatherTest2 : AppCompatActivity() {
    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 100
    }

    private val binding: ActivityWeatherTestBinding by lazy {
        ActivityWeatherTestBinding.inflate(layoutInflater)
    }
    private lateinit var temperature:TextView
    private lateinit var weatherView: ImageView
    private lateinit var weather: HashMap<LocalDate, HashMap<String, Int>>
    private lateinit var weatherApi: WeatherApi2
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        temperature = binding.awtWeatherTv
        weather = HashMap();
        weatherView = binding.awtIv
        weatherApi = WeatherApi2(this@WeatherTest2)

        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val currLoc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            Log.d("minseok", "Weather Request | Lat : " + currLoc!!.latitude + " Lng : " + currLoc.longitude)
            getWeather(weather, currLoc.latitude, currLoc.longitude)
        } else {
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(binding.root, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("확인") { ActivityCompat.requestPermissions(this@WeatherTest2, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE) }.show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.  // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeather(
        weather: HashMap<LocalDate, HashMap<String, Int>>,
        latitude: Double,
        longitude: Double
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = weatherApi.getWeather(weather, latitude, longitude)
                withContext(Dispatchers.Main) {
                    showWeatherInfo(LocalDate.now())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showWeatherInfo(date: LocalDate?) {
        if (weather.containsKey(date)) {
            binding.or.visibility = View.VISIBLE
            temperature.visibility = View.VISIBLE
            weatherView.visibility = View.VISIBLE
            if (weather[date]?.containsKey("TMP") == true) {
                temperature.text = weather[date]!!["TMP"].toString() + "ºC"
            } else {
                temperature.text = weather[date]?.get("MAX").toString() + "º/" + weather[date]?.get("MIN") + "ºC"
            }
            val pty: Int? = weather[date]?.get("PTY")
            val sky: Int? = weather[date]?.get("SKY")
            if (pty!! > 0) {
                when (pty) {
                    1, 4 -> weatherView.setImageResource(R.drawable.rainy_2_svgrepo_com)
                    2 -> weatherView.setImageResource(R.drawable.cloud_snow_rain_svgrepo_com)
                    else -> weatherView.setImageResource(R.drawable.snow_outline_svgrepo_com)
                }
            } else {
                when (sky) {
                    1 -> weatherView.setImageResource(R.drawable.ic_baseline_wb_sunny_24)
                    2 -> weatherView.setImageResource(R.drawable.cloudy_svgrepo_com)
                    else -> weatherView.setImageResource(R.drawable.ic_twotone_wb_cloudy_24)
                }
            }
        } else {
            binding.or.visibility = View.GONE
            temperature.visibility = View.GONE
            weatherView.visibility = View.GONE
        }
    }
}