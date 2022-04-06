package no.kristiania.pgr208

import android.content.Intent
import android.content.pm.PackageManager
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
import no.kristiania.pgr208.utils.URIPathHelper
import java.io.File


class MainActivity : AppCompatActivity() {

    private var imageFile: File? = null
    var uploadedImageURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        AndroidNetworking.initialize(applicationContext)

        val searchImages: Button = findViewById(R.id.searchResultsBtn)
        searchImages.setOnClickListener {
            val i = Intent(this, ReverseImageSearch::class.java)
            i.putExtra("Image_URL", uploadedImageURL)
            startActivity(i)
        }

//        UPLOAD IMAGE TO SERVER
        val uploadBtn: Button = findViewById(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            uploadImage()
        }


//        SELECT IMAGE FROM GALLERY
        val selectImageBtn: Button = findViewById(R.id.selectImageBtn)
        selectImageBtn.setOnClickListener {
            val i = Intent()
            i.action = Intent.ACTION_GET_CONTENT
            i.type = "image/*"
            startForResult.launch(i)
        }
    }

    private var startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uriPathHelper = URIPathHelper()
            val imageUri = it.data?.data
            val image: ImageView = findViewById(R.id.iv_userImage)
            image.setImageURI(imageUri)
            val imagePath = imageUri?.let { it1 -> uriPathHelper.getPath(this, it1) }
            if (!imagePath.isNullOrBlank())
                imageFile = File(imagePath)
            Toast.makeText(this, "Image selected", Toast.LENGTH_LONG).show()
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