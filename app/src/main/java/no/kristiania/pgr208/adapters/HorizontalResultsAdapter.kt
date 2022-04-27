package no.kristiania.pgr208.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208.data.DatabaseImage
import no.kristiania.pgr208.activities.FullScreenImageActivity
import no.kristiania.pgr208.R
import no.kristiania.pgr208.utils.BitmapHelper

class HorizontalResultsAdapter(private var context: Context, private val data: List<DatabaseImage>) :
    RecyclerView.Adapter<HorizontalResultsAdapter.MyViewHolder>() {
    class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val viewBtn: Button = itemView.findViewById(R.id.buttonView)
        fun bind(property: DatabaseImage) {
            val imageView = view.findViewById<ImageView>(R.id.iv_db_image)
            Glide.with(view.context).asBitmap().load(BitmapHelper.getBitmap(property.image))
                .centerCrop().into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.horizontal_images, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val viewButton = holder.viewBtn
        val imageBlob = data[position].image
        val imageId = data[position].id
        viewButton.setOnClickListener {
            val intent = Intent(context, FullScreenImageActivity::class.java)
            intent.putExtra("imageBlob", imageBlob )
            intent.putExtra("imageId", imageId)
            context.startActivity(intent)

        }
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}