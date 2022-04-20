package no.kristiania.pgr208.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208.DatabaseHandler
import no.kristiania.pgr208.DatabaseImage
import no.kristiania.pgr208.R

class SavedImagesAdapter(private val data: List<DatabaseImage>) :
    RecyclerView.Adapter<SavedImagesAdapter.MyViewHolder>() {

    class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var db: DatabaseHandler
        private lateinit var horizontalRecyclerView: RecyclerView
        private lateinit var horizontalManager: RecyclerView.LayoutManager
        private lateinit var horizontalAdapter: RecyclerView.Adapter<*>

        fun bind(property: DatabaseImage) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_results, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

}


