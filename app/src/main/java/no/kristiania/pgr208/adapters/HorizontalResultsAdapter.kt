package no.kristiania.pgr208.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208.DatabaseImage
import no.kristiania.pgr208.R
import no.kristiania.pgr208.utils.BitmapHelper
import java.util.*

class HorizontalResultsAdapter(private val data: List<DatabaseImage>) :
    RecyclerView.Adapter<HorizontalResultsAdapter.MyViewHolder>() {
    class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
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
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }
}