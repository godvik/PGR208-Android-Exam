package no.kristiania.pgr208.activities

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.kristiania.pgr208.data.Constants.baseUrl
import no.kristiania.pgr208.data.ImageUrls
import no.kristiania.pgr208.R
import no.kristiania.pgr208.adapters.ImageAdapter
import org.json.JSONArray


class ReverseImageSearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var manager: RecyclerView.LayoutManager
    private lateinit var myAdapter: RecyclerView.Adapter<*>

    //    Create an ArrayList of type ImageUrls which is a dataclass we created to store thumbnail and image links
    val list = ArrayList<ImageUrls>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_image_search)

//        Get the imageURL parcelable from the previous activity and send it along with endpoints to GET requests as long as its not null
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
    }

    // Search for similar images and add the image_link and thumbnail_links to our ArrayList

    private fun reverseImageSearch(imageUrl: String, endpoint: String) {
        val textView: TextView = findViewById(R.id.textView)
        textView.text = getString(R.string.similar_images, endpoint)
//        Move the GET operation to a coroutine on IO thread
        CoroutineScope(Dispatchers.IO).launch {
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
//                                    Get only the parts of the response we care about
                                    response.getJSONObject(i).getString("thumbnail_link"),
                                    response.getJSONObject(i).getString("image_link")
                                )
                            )
                        }
                        textView.text =
                            getString(R.string.results_found, response.length(), endpoint)
                        //  Use the list to inflate a recyclerview with images based of the thumbnail_link
                        manager = LinearLayoutManager(this@ReverseImageSearchActivity)
                        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
                            myAdapter = ImageAdapter(context, list)
                            layoutManager = manager
                            adapter = myAdapter
                        }
                    }

                    override fun onError(error: ANError) {
                        // Give the user some feedback if error
                        if (error.errorDetail.equals("connectionError")) {
                            textView.text = getString(R.string.search_timeout, endpoint)
                        } else {
                            textView.text = getString(R.string.upload_img_error, error.errorDetail)
                        }
                        println(error)
                        println(error.errorCode)
                        println(error.errorDetail)
                    }
                })
        }
    }
}

