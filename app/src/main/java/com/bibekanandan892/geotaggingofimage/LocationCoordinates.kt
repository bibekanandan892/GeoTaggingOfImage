package com.bibekanandan892.geotaggingofimage

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

@Composable
fun LocationCoordinates() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val latitude = remember { mutableStateOf(0.0) }
    val longitude = remember { mutableStateOf(0.0) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted, get the last known location
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        latitude.value = it.latitude
                        longitude.value = it.longitude
                    }
                }
            } else {
                // Permission denied, handle accordingly
            }
        }
    )

    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val hasPermission =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    if (!hasPermission) {
        // Request the location permission
        SideEffect {
            requestPermissionLauncher.launch(permission)
        }
    } else {
        // Permission already granted, get the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                latitude.value = it.latitude
                longitude.value = it.longitude
            }
        }
    }

    // Display the coordinates
    Text("Latitude: ${latitude.value}, Longitude: ${longitude.value}", color = Color.Green)
}

