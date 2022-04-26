package no.kristiania.pgr208

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import no.kristiania.pgr208.Constants.baseUrl
import no.kristiania.pgr208.utils.BitmapHelper.bitmapToFileUri
import no.kristiania.pgr208.utils.BitmapHelper.getBytes
import com.theartofdev.edmodo.cropper.CropImage
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.theartofdev.edmodo.cropper.CropImageView

import java.io.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private var imageFile: File? = null
    var uploadedImageURL: String? = null
    private var bitmap: Bitmap? = null
    private lateinit var db: DatabaseHandler

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(16, 9)
                .getIntent(this@MainActivity)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri {
            return CropImage.getActivityResult(intent)?.uri!!
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
        AndroidNetworking.initialize(applicationContext)
        AndroidNetworking.enableLogging()
        db = DatabaseHandler(this)
        val imgView = findViewById<ImageView>(R.id.iv_userImage)

        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) {
            it?.let { uri ->
                imgView.setImageURI(uri)
            }
        }


//        Select image from gallery, convert it to bitmap and set the imageView to the bitmap
//        Also takes the bitmap and gets the URI path to create a PNG file for upload
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { data ->
                val inputStream = contentResolver.openInputStream(data)
                bitmap = BitmapFactory.decodeStream(inputStream)
                imgView.setImageBitmap(bitmap)
                val imagePath = bitmapToFileUri(bitmap!!, this)
                imageFile = File(imagePath.toString())
            }

//        Launch the galleryPicker
        val selectImageBtn = findViewById<Button>(R.id.selectImageBtn)
        selectImageBtn.setOnClickListener {
            cropActivityResultLauncher.launch("image/*")
        }

//        Sends the uploadedImageURL to the next activity to be used for GET requests
        val searchImages: Button = findViewById(R.id.searchResultsBtn)
        searchImages.setOnClickListener {
            val i = Intent(this, ReverseImageSearchActivity::class.java)
            i.putExtra("Image_URL", uploadedImageURL)
            startActivity(i)

        }

//        Navigate to the results activity
        val savedResultsBtn: Button = findViewById(R.id.savedResultsBtn)
        savedResultsBtn.setOnClickListener {
            val i = Intent(this, DatabaseImagesActivity::class.java)
            startActivity(i)
        }
//        Upload image to server and save it to local db
        val uploadBtn: Button = findViewById(R.id.uploadBtn)
        uploadBtn.setOnClickListener {
            uploadImage()
        }

    }

    //    Uploads the previously created imageFile. On success it also saves the imageFile to the database as a BLOB
    private fun uploadImage() {
        val tvProgress: TextView = findViewById(R.id.tv_progress)
        tvProgress.text = getString(R.string.upload_img)
        AndroidNetworking.upload(baseUrl + "upload")
            .addHeaders("Content-Disposition:", "form-data")
            .addHeaders("Content-Type:", "image/png")
            .addMultipartFile("image", imageFile)
            .setPriority(Priority.HIGH)
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String) {
                    uploadedImageURL = response
                    db.addUploadedImage(DatabaseImage(0, getBytes(bitmap!!)))
                    tvProgress.text = getString(R.string.upload_img_success)
                }

                override fun onError(anError: ANError) {
                    println(anError.errorCode)
                    println(anError.message)
                    println(anError.errorDetail)
                    println(anError.errorBody)
                    tvProgress.text = getString(R.string.upload_img_error, anError.errorDetail)
                }
            })
    }

    //    Check is the user has granted the app proper file permissions. If not, it requests it. Gets called in onCreate
    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permission = mutableListOf<String>()

        if (!hasPermission()) {
            permission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permission.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permission.toTypedArray(), 0)
        }
    }
}