package com.example.breathwatch.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

object LocationHelper {
    
    private const val LOCATION_TIMEOUT = 10000L // 10 seconds

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun getCurrentLocation(
        context: Context,
        onLocationReceived: (latitude: Double, longitude: Double) -> Unit,
        onLocationError: ((String) -> Unit)? = null
    ) {
        if (!hasLocationPermission(context)) {
            onLocationError?.invoke("Location permission not granted")
            return
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            onLocationError?.invoke("Location services are disabled")
            return
        }

        val fusedLocationClient: FusedLocationProviderClient = 
            LocationServices.getFusedLocationProviderClient(context)
        
        try {
            val cancellationTokenSource = CancellationTokenSource()
            
            // Set timeout
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                cancellationTokenSource.cancel()
            }, LOCATION_TIMEOUT)

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    onLocationReceived(location.latitude, location.longitude)
                } else {
                    // Try to get last known location as fallback
                    getLastKnownLocation(context)?.let { lastLocation ->
                        onLocationReceived(lastLocation.latitude, lastLocation.longitude)
                    } ?: onLocationError?.invoke("Unable to get location")
                }
            }.addOnFailureListener { exception ->
                onLocationError?.invoke(exception.message ?: "Failed to get location")
            }
        } catch (e: SecurityException) {
            onLocationError?.invoke("Location permission denied")
        } catch (e: Exception) {
            onLocationError?.invoke("Error getting location: ${e.message}")
        }
    }

    fun getLastKnownLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) {
            return null
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            // Try GPS provider first
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let { return it }

            // Fall back to network provider
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.let { return it }

            // Finally try passive provider
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } catch (e: SecurityException) {
            return null
        }
    }
    
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
