package no.kristiania.pgr208.activities


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208.R
import no.kristiania.pgr208.adapters.SavedImagesAdapter
import no.kristiania.pgr208.utils.DatabaseHandler

class DatabaseImagesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    private lateinit var db: DatabaseHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_results)
        db = DatabaseHandler(this)
        recyclerView = findViewById(R.id.recycler_view)
        textView = findViewById(R.id.textView)

    }

    //        Create X amount of cards based on the amount of results from db.getIds()
    override fun onStart() {
        super.onStart()
//        Give the user some feedback if the database is empty
        if (db.getIds().size == 0) {
            textView.text = getString(R.string.empty_db)
        } else {
            textView.text = ""
        }

        manager = LinearLayoutManager(this)
        recyclerView.apply {
            myAdapter = SavedImagesAdapter(db.getIds())
            layoutManager = manager
            adapter = myAdapter
        }

    }

}