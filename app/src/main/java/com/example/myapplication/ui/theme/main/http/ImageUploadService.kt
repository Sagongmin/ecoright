package com.example.myapplication.ui.theme.main.http

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ImageUploadService {
    @Multipart
    @POST("upload")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ResponseBody>
}