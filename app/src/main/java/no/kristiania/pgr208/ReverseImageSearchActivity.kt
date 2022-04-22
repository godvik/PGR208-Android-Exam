package no.kristiania.pgr208

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import no.kristiania.pgr208.Constants.baseUrl
import no.kristiania.pgr208.adapters.ImageAdapter
import org.json.JSONArray


class ReverseImageSearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>
    val list = ArrayList<ImageUrls>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_image_search)

//        Get the imageURL intent from the previous activity and send it along with endpoints to GET requests as long as its not null
        val imageUrl = intent.getStringExtra("Image_URL")

        val searchGoogleBtn: Button = findViewById(R.id.searchGoogle)
        searchGoogleBtn.setOnClickListener {
            if (imageUrl != null) {
                reverseImageSearch(imageUrl, "google")
            }
        }

        val searchBingBtn: Button = findViewById(R.id.searchBing)
        searchBingBtn.setOnClickListener {
            if (imageUrl != null) {
                reverseImageSearch(imageUrl, "bing")
            }
        }

        val searchTineyeBtn: Button = findViewById(R.id.searchTineye)
        searchTineyeBtn.setOnClickListener {
            if (imageUrl != null) {
                reverseImageSearch(imageUrl, "tineye")
            }
        }

        manager = LinearLayoutManager(this)

    }

    // Search for similar images and add the image_link and thumbnail_links to an Object ImageProperty and add to a list
//  Use the list to inflate a recyclerview with images based of the thumbnail_link
    private fun reverseImageSearch(imageUrl: String, endpoint: String) {
        val textView: TextView = findViewById(R.id.textView)
        textView.text = getString(R.string.similar_images, endpoint)
        AndroidNetworking.get(baseUrl + endpoint)
            .addQueryParameter("url", imageUrl)
            .setTag("test")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    for (i in 0 until response.length()) {
                        list.add(
                            ImageUrls(
                                response.getJSONObject(i).getString("thumbnail_link"),
                                response.getJSONObject(i).getString("image_link")
                            )
                        )
                    }
                    textView.text = getString(R.string.results_found, response.length(), endpoint)
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
