package no.kristiania.pgr208.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object BitmapHelper {
    fun getBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    fun getBitmap(byteArray: ByteArray): Bitmap {
        val stream = ByteArrayInputStream(byteArray)
        return BitmapFactory.decodeStream(stream)
    }
}