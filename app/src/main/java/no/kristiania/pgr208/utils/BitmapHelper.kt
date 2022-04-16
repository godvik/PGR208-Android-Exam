package no.kristiania.pgr208.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

object BitmapHelper {
    fun getBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }
}