package no.kristiania.pgr208

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208.adapters.HorizontalResultsAdapter
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

        manager = LinearLayoutManager(this)

//        Create X amount of cards based on the amount of results from db.viewImage()

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            myAdapter = SavedImagesAdapter(db.viewImage())
            layoutManager = manager
            adapter = myAdapter
        }
    }
}