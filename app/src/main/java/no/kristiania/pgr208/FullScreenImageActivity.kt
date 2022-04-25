package no.kristiania.pgr208

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class FullScreenImageActivity: AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUrl = intent.getStringExtra("image_link")

        if (imageUrl.isNullOrBlank()) {
            setContentView(R.layout.activity_main)
        } else {
        setContentView(R.layout.activity_fullscreen)
        }

        val imgView = findViewById<ImageView>(R.id.iv_full)
        val btnClose = findViewById<Button>(R.id.btnClose)

        btnClose.setOnClickListener {
            finish()
        }

        Glide.with(this).load(imageUrl).into(imgView)
    }
}
