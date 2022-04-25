package no.kristiania.pgr208

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import no.kristiania.pgr208.utils.BitmapHelper.getBitmap

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHandler(this)
        val imageUrl = intent.getStringExtra("image_link")
        val imageBlob = intent.getByteArrayExtra("imageBlob")
        val imageId = intent.getIntExtra("imageId", -1)


        if (imageUrl.isNullOrBlank()) {
            setContentView(R.layout.activity_fullscreen_delete)
        } else {
            setContentView(R.layout.activity_fullscreen)
        }

        val imgView = findViewById<ImageView>(R.id.iv_full)
        val btnClose = findViewById<Button>(R.id.btnClose)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        btnDelete.setOnClickListener {
            Toast.makeText(this, "Image deleted!", Toast.LENGTH_SHORT).show()
            db.deleteImage(imageId)
            finish()
        }

        btnClose.setOnClickListener {
            finish()
        }

        if (imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageBlob).into(imgView)
        } else {
            Glide.with(this).load(imageUrl).into(imgView)
        }

    }
}
