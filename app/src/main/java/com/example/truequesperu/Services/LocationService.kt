package com.example.truequesperu.Services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class LocationService(private val activity: Activity) {

    private var fusedLocation = LocationServices.getFusedLocationProviderClient(activity)

    companion object {
        const val PERMISSION_ID = 1001
    }

    fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(callback: (Double, Double) -> Unit) {
        if (!checkPermissions()) {
            requestPermissions()
            return
        }

        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback(location.latitude, location.longitude)
            } else {
                requestNewLocation(callback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation(callback: (Double, Double) -> Unit) {
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 3000L
        ).setWaitForAccurateLocation(true).build()

        fusedLocation.requestLocationUpdates(
            request,
            object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val loc = result.lastLocation
                    if (loc != null) {
                        callback(loc.latitude, loc.longitude)
                        fusedLocation.removeLocationUpdates(this)
                    }
                }
            },
            activity.mainLooper
        )
    }
}