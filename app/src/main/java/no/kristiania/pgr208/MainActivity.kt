package no.kristiania.pgr208

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var imgView: ImageView
    private var imageFile: File? = null
    var uploadedImageURL: String? = null
    private var bitmap: Bitmap? = null

    lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        AndroidNetworking.initialize(applicationContext)

        db = DatabaseHandler(this)

        imgView = findViewById(R.id.iv_userImage)
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { data ->
                val inputStream = contentResolver.openInputStream(data)
                bitmap = BitmapFactory.decodeStream(inputStream)
                imgView.setImageBitmap(bitmap)
                var imagePath = bitmapToFile(bitmap!!)
                imageFile = File(imagePath.toString())
            }

        val searchImages: Button = findViewById(R.id.searchResultsBtn)
        searchImages.setOnClickListener {
            val i = Intent(this, ReverseImageSearch::class.java)
            i.putExtra("Image_URL", uploadedImageURL)
            startActivity(i)

        }

//        Upload image to server and save it to local db
        val uploadBtn: Button = findViewById(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            uploadImage()
            db.addUploadedImage(DatabaseImage(1, getBytes(bitmap!!)))
        }


//        SELECT IMAGE FROM GALLERY
        val selectImageBtn: Button = findViewById(R.id.selectImageBtn)
        selectImageBtn.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    fun getBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }


    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.png")

        try {
            // Compress the bitmap and save in png format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }


    private fun uploadImage() {
        val tvProgress: TextView = findViewById(R.id.tv_progress)
        AndroidNetworking.upload("http://api-edu.gtl.ai/api/v1/imagesearch/upload")
            .addHeaders("Content-Disposition:", "form-data")
            .addHeaders("Content-Type:", "image/png")
            .addMultipartFile("image", imageFile)
            .setPriority(Priority.HIGH)
            .build()
            .setUploadProgressListener { bytesUploaded, totalBytes -> // do anything with progress
                tvProgress.text = "Uploading image.."
            }
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String) {
                    Toast.makeText(this@MainActivity, response, Toast.LENGTH_SHORT).show()
                    uploadedImageURL = response
                    tvProgress.text = "Image uploaded successfully!"
                }

                override fun onError(anError: ANError) {
                    Toast.makeText(this@MainActivity, anError.message, Toast.LENGTH_SHORT)
                        .show()
                    println(anError)
                    tvProgress.text = "An error occured while uploading image"
                }
            })
    }

    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        var permission = mutableListOf<String>()

        if (!hasPermission()) {
            permission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 0)
        }
    }
}