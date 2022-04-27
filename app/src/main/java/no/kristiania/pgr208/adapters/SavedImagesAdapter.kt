package no.kristiania.pgr208.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208.utils.DatabaseHandler
import no.kristiania.pgr208.R

class SavedImagesAdapter(private val data: ArrayList<Int>) :
    RecyclerView.Adapter<SavedImagesAdapter.MyViewHolder>() {
    private lateinit var db: DatabaseHandler
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    //    Creates X amount of cards from SavedResultsActivity
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_db_images, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        db = DatabaseHandler(holder.itemView.context)
        manager =
            LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)

        //        Fills the cards horizontally with images from db
        holder.itemView.findViewById<RecyclerView>(R.id.horizontal_recyclerView).apply {
            myAdapter = HorizontalResultsAdapter(context, db.getRelatedImages(data[position]))
            layoutManager = manager
            adapter = myAdapter
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}


