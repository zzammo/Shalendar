package com.ddmyb.shalendar.view.schedules.model.service

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.Locale

class GeoCodingService {
    fun getAddress(latLng: LatLng, context: Context?): String? {
        val geocoder = Geocoder(context!!, Locale.getDefault())
        val addresses: List<Address>? = try {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )
        } catch (ioException: IOException) {
            Log.e("geoCoder", ioException.message!!)
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return null
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return null
        }
        return if (addresses.isNullOrEmpty()) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            null
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }
}