package com.example.exploralocal.util


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageUtils {

    /**
     * Creates a temporary file where the photo will be stored
     */
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            "JPEG_${timeStamp}_",  // prefix
            ".jpg",                 // suffix
            storageDir              // directory
        )
    }

    /**
     * Get a thumbnail of the full-sized image for preview
     */
    fun getImageThumbnail(filePath: String, maxSize: Int = 800): Bitmap? {
        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(filePath, bmOptions)

        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = Math.max(1, Math.min(photoW / maxSize, photoH / maxSize))

        bmOptions.apply {
            inJustDecodeBounds = false
            inSampleSize = scaleFactor
        }

        return BitmapFactory.decodeFile(filePath, bmOptions)
    }

    /**
     * Share an image with other apps
     */
    fun getSharedImageFile(context: Context, originalPath: String): File {
        val sharedImageFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "shared_image.jpg"
        )

        File(originalPath).copyTo(sharedImageFile, overwrite = true)
        return sharedImageFile
    }
}