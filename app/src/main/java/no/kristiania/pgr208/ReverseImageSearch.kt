package no.kristiania.pgr208

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.error.ANError

import org.json.JSONArray
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority


class ReverseImageSearch : AppCompatActivity() {


    private val baseUrl: String = "http://api-edu.gtl.ai/api/v1/imagesearch/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_image_search)

        val imageUrl = intent.getStringExtra("Image_URL")

        val textView: TextView = findViewById(R.id.textView)
        textView.setText(imageUrl)

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
        }


    }

    private fun reverseImageSearch(baseUrl: String, imageUrl: String, site: String) {
        Toast.makeText(this, "Making get request to $site endpoint", Toast.LENGTH_LONG).show()

        AndroidNetworking.get(baseUrl + site)
            .addQueryParameter("url", imageUrl)
            .setTag("test")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    // do anything with response
                    // Printing thumbnail links for now..
                    for (i in 0 until response.length()) {
                        println(response.getJSONObject(i).getString("thumbnail_link"))
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