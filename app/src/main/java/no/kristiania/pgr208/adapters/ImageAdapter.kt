package no.kristiania.pgr208.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208.DatabaseHandler
import no.kristiania.pgr208.DatabaseImage
import no.kristiania.pgr208.ImageUrls
import no.kristiania.pgr208.R
import no.kristiania.pgr208.utils.BitmapHelper

class ImageAdapter(private val data: List<ImageUrls>) :
    RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {


    class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var db: DatabaseHandler
        fun bind(property: ImageUrls) {
            val title = view.findViewById<TextView>(R.id.tvTitle)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val saveBtn = view.findViewById<Button>(R.id.buttonSave)
            val viewBtn = view.findViewById<Button>(R.id.buttonView)

            db = DatabaseHandler(view.context)

//            Get bitmap from the imageView and add to db
            saveBtn.setOnClickListener {
                val drawable = imageView.drawable
                val bitmap = drawable.toBitmap()
                db.addSavedImage(DatabaseImage(1, BitmapHelper.getBytes(bitmap)))
                title.text = view.context.getString(R.string.saved_db)
            }

            viewBtn.setOnClickListener {
                // TODO: 16/04/2022 View image in fullscreen
            }

            Glide.with(view.context).load(property.image).centerCrop().into(imageView)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_searchresult_images, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }


}