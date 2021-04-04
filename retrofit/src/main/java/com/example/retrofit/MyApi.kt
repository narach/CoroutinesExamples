package com.example.retrofit

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface MyApi {
    @GET("/comments")
    fun getComments(): Call<List<Comment>>

    @GET("/comments")
    suspend fun getCommentsSusp(): Response<List<Comment>>
}