package com.bibekanandan892.geotaggingofimage

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.bibekanandan892.geotaggingofimage.ui.theme.GeoTaggingOfImageTheme
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            val image = remember{ mutableStateOf<ByteArray?>(null) }
            val context = LocalContext.current
            val imgUri by remember{ mutableStateOf("${context.filesDir.absoluteFile}/temp.jpg")}
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
            val imageUploadLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                    if (it != null) {
                        saveImageToInternalStorage(context = context, bitmap = it,latitude = latitude.value,longitude = longitude.value)
                        image.value = it.toByteArray()
                    } else {
                        Toast.makeText(context, "Select Image with size 200KB", Toast.LENGTH_SHORT).show()
                    }
                }
            GeoTaggingOfImageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(Modifier.fillMaxWidth()) {
                            Button(onClick = {
                                imageUploadLauncher.launch(null)
                            }) {
                                Text(text = "open camera")
                            }
                            Image(painter = rememberAsyncImagePainter(model = image.value), contentDescription = "")
                            Text(
                                "Latitude: ${latitude.value}, Longitude: ${longitude.value}",
                                color = Color.Green
                            )

                        }
                    }

                }
            }
            

        }
    }
}















fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

