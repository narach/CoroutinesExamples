package com.example.retrofit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.retrofit.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://jsonplaceholder.typicode.com/"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivityRetrofit"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApi::class.java)

        // Normal Retrofit Flow with callbacks
        api.getComments().enqueue(object :
            Callback<List<Comment>> { // Starts a new Thread, which is resource-consuming
            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Log.e(TAG, "ERROR: $t")
            }

            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        for (comment in it) {
                            Log.d(TAG, comment.toString())
                        }
                    }
                }
            }
        })

        // Coroutines way.
        GlobalScope.launch(Dispatchers.IO) {
            val comments = api.getComments().await()
            for (comment in comments) {
                Log.d(TAG, comment.toString())
            }

            // With Response flow
            val response = api.getComments().awaitResponse()
            if (response.isSuccessful) {
                for (comment in response.body()!!) {
                    Log.d(TAG, comment.toString())
                }
            }

            // With suspend fun
            val response2 = api.getCommentsSusp()
            if(response.isSuccessful) {
                for(comment in response2.body()!!) {
                    Log.d(TAG, comment.toString())
                }
            }
        }
    }
}