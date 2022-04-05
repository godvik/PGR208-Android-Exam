package no.kristiania.pgr208

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import android.provider.MediaStore
import android.widget.TextView
import java.io.File
import com.androidnetworking.interfaces.StringRequestListener


class MainActivity : AppCompatActivity() {

    private var imageFile: File? = null
    var uploadedImageURL: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }
    }


    private fun getRealPathFromURI(contentURI: Uri?, context: Activity): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.managedQuery(
            contentURI, projection, null,
            null, null
        ) ?: return null
        val columnIndex = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        return if (cursor.moveToFirst()) {
            // cursor.close();
            cursor.getString(columnIndex)
        } else null
        // cursor.close();
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            val imageUri = data?.data
            val image: ImageView = findViewById(R.id.iv_userImage)
            image.setImageURI(imageUri)
            val imagepath = getRealPathFromURI(imageUri, this)
            imageFile = File(imagepath)
            Toast.makeText(this, "Image selected", Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImage() {
//        Currently have to manually give permissions in emulator
//        TODO Write code to ask for WRITE_EXTERNAL_STORAGE permissions
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
                    println(response)
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
}