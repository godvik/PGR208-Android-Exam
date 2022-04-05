package no.kristiania.pgr208

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.androidnetworking.error.ANError

import org.json.JSONArray
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority


class ReverseImageSearch : AppCompatActivity() {


private val baseUrl : String = "http://api-edu.gtl.ai/api/v1/imagesearch/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reverse_image_search)

        val imageUrl = intent.getStringExtra("Image_URL")

        val textView: TextView = findViewById(R.id.textView)
        textView.setText(imageUrl)
        if (imageUrl != null) {
            searchGoogle(baseUrl, imageUrl)
        }
    }

    private fun searchGoogle(baseUrl: String, imageUrl: String) {

        AndroidNetworking.get(baseUrl + "google")
            .addPathParameter("url", imageUrl)
//            .addQueryParameter("limit", "3")
            .setTag("test")
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    // do anything with response
                    println("!!--------------------!!")
                    println(response)
                    println("??--------------------??")
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