package no.kristiania.pgr208

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import org.json.JSONArray


class ReverseImageSearch : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>

    val list = ArrayList<ImageProperty>()

    private val baseUrl: String = "http://api-edu.gtl.ai/api/v1/imagesearch/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_image_search)


        val imageUrl = intent.getStringExtra("Image_URL")

        val searchGoogleBtn: Button = findViewById(R.id.searchGoogle)
        searchGoogleBtn.setOnClickListener {
            if (imageUrl != null) {
                reverseImageSearch(baseUrl, imageUrl, "google")
            }
        }

        val searchBingBtn: Button = findViewById(R.id.searchBing)
        searchBingBtn.setOnClickListener {
            if (imageUrl != null) {
                reverseImageSearch(baseUrl, imageUrl, "bing")
            }
        }

        val searchTineyeBtn: Button = findViewById(R.id.searchTineye)
        searchTineyeBtn.setOnClickListener {
            if (imageUrl != null) {
                reverseImageSearch(baseUrl, imageUrl, "tineye")
            }
            addRecord()
        }

        manager = LinearLayoutManager(this)

    }


    fun addRecord() {
        val thumbnail = "https://th.bing.com/th/id/OIP.e0eE8jT_sqZo38r8ywfalAAAAA?pid=ImgDet&w=207&h=207&c=7"
        val image = "https://th.bing.com/th/id/OIP.e0eE8jT_sqZo38r8ywfalAAAAA?pid=ImgDet&w=207&h=207&c=7"
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        val status = databaseHandler.addImage(DatabaseImage(0, thumbnail, image))

        if (status > -1) {
            Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_LONG).show()
        }

        println(databaseHandler.viewImage())
    }

    private fun reverseImageSearch(baseUrl: String, imageUrl: String, endpoint: String) {
        val textView: TextView = findViewById(R.id.textView)
        textView.setText("Searching for similar images on $endpoint..")

        AndroidNetworking.get(baseUrl + endpoint)
            .addQueryParameter("url", imageUrl)
            .setTag("test")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    for (i in 0 until response.length()) {
                        list.add(
                            ImageProperty(
                                response.getJSONObject(i).getString("thumbnail_link"),
                                response.getJSONObject(i).getString("image_link")
                            )
                        )
                    }
                    textView.setText("Found ${response.length()} results from $endpoint")
                    recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
                        myAdapter = ImageAdapter(list)
                        layoutManager = manager
                        adapter = myAdapter
                    }

                }

                override fun onError(error: ANError) {
                    // handle error
                    println(error)
                    println(error.errorCode)
                    println(error.errorDetail)
                }
            })
    }
}

