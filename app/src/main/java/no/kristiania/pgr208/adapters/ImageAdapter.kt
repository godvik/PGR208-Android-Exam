package no.kristiania.pgr208.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208.*
import no.kristiania.pgr208.activities.FullScreenImageActivity
import no.kristiania.pgr208.data.DatabaseImage
import no.kristiania.pgr208.data.ImageUrls
import no.kristiania.pgr208.utils.BitmapHelper
import no.kristiania.pgr208.utils.DatabaseHandler

class ImageAdapter(private var context: Context, private val data: List<ImageUrls>) :
    RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

    class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var db: DatabaseHandler
        val viewBtn: Button = itemView.findViewById(R.id.buttonView)

        fun bind(property: ImageUrls) {
            val title = view.findViewById<TextView>(R.id.tvTitle)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val saveBtn = view.findViewById<Button>(R.id.buttonSave)

            db = DatabaseHandler(view.context)

//            Get bitmap from the imageView and add to db
            saveBtn.setOnClickListener {
                if (imageView.drawable != null) {
                    val drawable = imageView.drawable
                    val bitmap = drawable.toBitmap()
                    db.addSavedImage(DatabaseImage(1, BitmapHelper.getBytes(bitmap), ""))
                    title.text = view.context.getString(R.string.saved_db)
                } else {
                    title.text = view.context.getString(R.string.no_image)
                }
            }

            Glide.with(view.context).load(property.image).error(R.drawable.broken).centerCrop().into(imageView)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_searchresult_images, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val buttonView = holder.viewBtn
        val imageUrl = data[position].image

        buttonView.setOnClickListener {
            run {
                val intent = Intent(context, FullScreenImageActivity::class.java)
                intent.putExtra("image_link", imageUrl )
                context.startActivity(intent)
            }
        }
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }


}