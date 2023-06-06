package com.bibekanandan892.geotaggingofimage

import android.content.Context
import android.media.ExifInterface
import android.media.ExifInterface.TAG_GPS_LATITUDE
import android.media.ExifInterface.TAG_GPS_LONGITUDE
import android.net.Uri
import java.io.File
import java.io.IOException

fun addGeotaggingToImage(context: Context, latitude: Double, longitude: Double,imageUri : Uri) {
//    val imageFile = File("${context.filesDir}/Pictures/YourFolderName", "your_image.jpg") // Replace "your_image.jpg" with the actual image file name
//    val tempFile = File(context.cacheDir, "temp_image.jpg")

    try {

        val imageFile: File? = imageUri.path?.let { File(it) }
//        val filename = imageFile.canonicalPath
//        exif = ExifInterface(filename)
//        exif.setAttribute(TAG_GPS_LATITUDE, GPS.convert(latitude))
//        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(latitude))
//        exif.setAttribute(TAG_GPS_LONGITUDE, GPS.convert(longitude))
//        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(longitude))
//        exif.saveAttributes()

        // Copy the original image file to a temporary file
//        imageFile?.copyTo(tempFile, overwrite = true)

        // Create an ExifInterface instance for the temporary image file
        val exifInterface = imageFile?.absolutePath?.let { ExifInterface(it) }

        // Set the latitude and longitude values in the EXIF metadata
        exifInterface?.setAttribute(TAG_GPS_LATITUDE,latitude.toString())
        exifInterface?.setAttribute(TAG_GPS_LONGITUDE,longitude.toString())
        // Save the updated EXIF metadata to the temporary image file
        exifInterface?.saveAttributes()

        // Move the temporary file to replace the original image file
//        tempFile.renameTo(imageFile)
    } catch (e: IOException) {
        e.printStackTrace()
        // Handle the exception
    }
}
