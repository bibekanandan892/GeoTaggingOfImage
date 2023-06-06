package com.bibekanandan892.geotaggingofimage

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object ImageUtils {

    fun addGeoTag(bitmap: Bitmap, location: Location, outputPath: String) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Create a new file in the specified output path
        val fileName = "IMG_$timestamp.jpg"
        val imageFile = FileOutputStream("$outputPath/$fileName")

        // Save the bitmap with geotag information to the new file
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageFile)
        imageFile.flush()
        imageFile.close()

        // Update the new file with geotag information
        val exifInterface = ExifInterface("$outputPath/$fileName")
//        exifInterface.se(location.latitude, location.longitude)
        exifInterface.saveAttributes()
    }
}

@RequiresApi(Build.VERSION_CODES.R)
fun saveImageToInternalStorage(
    context: Context,
    bitmap: Bitmap,
    latitude: Double,
    longitude: Double
) {
    val contentResolver = context.contentResolver
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "imageasdf.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/YourFolderName")
    }

    // Use MediaStore to insert the image into the device's media store
    val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    // Open an output stream to write the bitmap data to the image URI
    imageUri?.let { uri ->
        val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
        outputStream?.use {
            // Compress the bitmap to JPEG format and write it to the output stream
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, it)
            it.flush()
        }
        addGeotaggingToImageq(context = context,latitude = latitude, longitude = longitude, imageUri = uri)
    }
}


fun addGeotaggingToImageq(context: Context, imageUri: Uri, latitude: Double, longitude: Double) {
    val imagePath = getImagePathFromUri(context, imageUri)
    if (imagePath != null) {
        try {
            val exifInterface = ExifInterface(imagePath)

            // Set latitude
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, formatLatLong(latitude))
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, if (latitude >= 0) "N" else "S")

            // Set longitude
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, formatLatLong(longitude))
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, if (longitude >= 0) "E" else "W")

            exifInterface.saveAttributes()
        } catch (e: IOException) {
            // Handle the exception
        }
    }
}

private fun getImagePathFromUri(context: Context, imageUri: Uri): String? {
    val contentResolver = context.contentResolver
    val imageProjection = arrayOf(android.provider.MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(imageUri, imageProjection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            return it.getString(columnIndex)
        }
    }
    return null
}

private fun formatLatLong(latLong: Double): String {
    val absLatLong = Math.abs(latLong)
    val degrees = absLatLong.toInt()
    val minutes = (absLatLong - degrees) * 60
    val seconds = (minutes - minutes.toInt()) * 60

    return "$degrees/1,$minutes/1,$seconds/1"
}


