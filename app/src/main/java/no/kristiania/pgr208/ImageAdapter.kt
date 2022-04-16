package no.kristiania.pgr208

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream

class ImageAdapter(private val data: List<ImageProperty>) :
    RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {



    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var db: DatabaseHandler
        fun bind(property: ImageProperty) {
            val title = view.findViewById<TextView>(R.id.tvTitle)
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val saveBtn = view.findViewById<Button>(R.id.buttonSave)
            val viewBtn = view.findViewById<Button>(R.id.buttonView)

            db = DatabaseHandler(view.context)


            saveBtn.setOnClickListener {
                val drawable = imageView.drawable
                val bitmap = drawable.toBitmap()

                db.addSavedImage(DatabaseImage(1, getBytes(bitmap)))
                Toast.makeText(view.context, "Added to db", Toast.LENGTH_SHORT).show()
            }

            viewBtn.setOnClickListener {
                // TODO: 16/04/2022 View image in fullscreen
            }

            Glide.with(view.context).load(property.image).centerCrop().into(imageView)
        }
        fun getBytes(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
            return stream.toByteArray()
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_photo, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }




}