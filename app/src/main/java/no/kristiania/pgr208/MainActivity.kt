package no.kristiania.pgr208

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import kotlinx.android.synthetic.main.activity_main.*
import no.kristiania.pgr208.Constants.baseUrl
import no.kristiania.pgr208.utils.BitmapHelper.bitmapToFileUri
import no.kristiania.pgr208.utils.BitmapHelper.getBytes
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var imgView: ImageView
    private lateinit var tvProgress: TextView
    private var imageFile: File? = null
    var uploadedImageURL: String? = null
    private var bitmap: Bitmap? = null
    private lateinit var db: DatabaseHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()

//        Set global timeout for android networking
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
        AndroidNetworking.initialize(applicationContext, okHttpClient)
        AndroidNetworking.enableLogging()
        db = DatabaseHandler(this)
        imgView = findViewById(R.id.iv_userImage)
        tvProgress = findViewById(R.id.tv_progress)


//        Sends the uploadedImageURL to the next activity to be used for GET requests
        val searchImages: Button = findViewById(R.id.searchResultsBtn)
        searchImages.setOnClickListener {
            val i = Intent(this, ReverseImageSearchActivity::class.java)
            i.putExtra("Image_URL", uploadedImageURL)
            startActivity(i)

        }

//        Upload image to server and save it to local db
        val uploadBtn: Button = findViewById(R.id.uploadBtn)
        uploadBtn.visibility = View.INVISIBLE
        uploadBtn.setOnClickListener {
            uploadImage()
        }

//        Pick an image from camera or gallery and crop it
        val selectImageBtn: Button = findViewById(R.id.selectImageBtn)
        selectImageBtn.setOnClickListener {
            startCrop()
        }

//        Navigate to the results activity
        val savedResultsBtn: Button = findViewById(R.id.savedResultsBtn)
        savedResultsBtn.setOnClickListener {
            val i = Intent(this, DatabaseImagesActivity::class.java)
            startActivity(i)
        }
    }

    override fun onStart() {
        super.onStart()
        tvProgress.text = ""

    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
            }
        )
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val inputStream = result.uriContent?.let { contentResolver.openInputStream(it) }
            bitmap = BitmapFactory.decodeStream(inputStream)
            imgView.setImageBitmap(bitmap)
            val imagePath = bitmapToFileUri(bitmap!!, this)
            imageFile = File(imagePath.toString())
            uploadBtn.visibility = View.VISIBLE
        } else {
            tvProgress.text = result.error.toString()
        }
    }


    //    Uploads the previously created imageFile. On success it also saves the imageFile to the database as a BLOB
    private fun uploadImage() {
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