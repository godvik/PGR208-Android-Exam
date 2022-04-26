package no.kristiania.pgr208

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208.adapters.SavedImagesAdapter

class DatabaseImagesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    private lateinit var db: DatabaseHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_results)
        db = DatabaseHandler(this)
        recyclerView = findViewById(R.id.recycler_view)
        manager = LinearLayoutManager(this)



    }
    //        Create X amount of cards based on the amount of results from db.getIds()
    override fun onStart() {
        super.onStart()
        recyclerView.apply {
            myAdapter = SavedImagesAdapter(db.getIds())
            layoutManager = manager
            adapter = myAdapter
        }
    }


}