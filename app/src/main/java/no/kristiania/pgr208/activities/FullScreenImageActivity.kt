package no.kristiania.pgr208.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import no.kristiania.pgr208.fragments.DeleteDialogFragment
import no.kristiania.pgr208.R
import no.kristiania.pgr208.utils.DatabaseHandler

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var db: DatabaseHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = DatabaseHandler(this)

//        Get the parcelables
        val imageUrl = intent.getStringExtra("image_link")
        val imageBlob = intent.getByteArrayExtra("imageBlob")
        val imageId = intent.getIntExtra("imageId", -1)
        val columnName = intent.getStringExtra("columnName")

//        We use the same activity for 2 slightly different layouts
//        if the image_link intent is null, we show the layout with a delete button for saved images
//        We use the elvis operator to determine which layout should be shown by checking if imageUrl is null

        imageUrl?.let { setContentView(R.layout.activity_fullscreen) }
            ?: setContentView(R.layout.activity_fullscreen_delete)


        val imgView = findViewById<ImageView>(R.id.iv_full)
        val btnClose = findViewById<Button>(R.id.btnClose)
        val btnDelete = findViewById<Button>(R.id.btnDelete)


//        Pass the imageId to a confirm deletion dialog fragment
        btnDelete?.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("imageId", imageId)
            bundle.putString("columnName", columnName)
            val dialog = DeleteDialogFragment()
            dialog.arguments = bundle
            dialog.show(supportFragmentManager, "deleteDialog")
        }


        btnClose.setOnClickListener {
            finish()
        }

//        Load the imageview with either blob or url, depending on which layout is active
        if (imageUrl.isNullOrEmpty()) {
            Glide.with(this).load(imageBlob).into(imgView)
        } else {
            Glide.with(this).load(imageUrl).into(imgView)
        }

    }
}
