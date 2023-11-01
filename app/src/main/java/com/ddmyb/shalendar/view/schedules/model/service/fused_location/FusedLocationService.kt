package com.ddmyb.shalendar.view.schedules.model.service.fused_location

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

class FusedLocationService {

    private val mFusedLocationClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest
    constructor(activity: Activity){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(Long.MAX_VALUE)
            .setFastestInterval(Long.MAX_VALUE)
        LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
    }
    @SuppressLint("MissingPermission")
    fun updateLiveLocation(locationCallback: LocationCallback){
        mFusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }
}