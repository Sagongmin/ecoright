package com.example.myapplication.ui.theme.main.http

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class imageUploader(private val context: Context) {
    fun uploadImage(file: File) {
        val file = File(file.path) // URI에서 파일을 얻습니다.

        // 파일을 멀티파트 바디로 변환합니다.
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val client = OkHttpClient.Builder()
            .connectTimeout(1000, TimeUnit.SECONDS) // 연결 타임아웃 시간을 60초로 설정
            .readTimeout(1000, TimeUnit.SECONDS)    // 읽기 타임아웃 시간을 60초로 설정
            .writeTimeout(1000, TimeUnit.SECONDS)   // 쓰기 타임아웃 시간을 60초로 설정
            .build()

        // Retrofit 인스턴스를 생성합니다.
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.200.109:25565")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        // 이미지 업로드 서비스를 생성합니다.
        val service = retrofit.create(ImageUploadService::class.java)

        // 이미지를 서버로 전송합니다.
        val call = service.uploadImage(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // 서버로부터 받은 OCR 결과 처리
                    val ocrResult = response.body()?.string() ?: return
                    Log.d("OCR Result", ocrResult ?: "No result") // 결과 로그로 출력
                    val parsedTexts = parseOcrResults(ocrResult)
                    saveTextToFile(parsedTexts, "ocr_results.txt")

                    val internalFilePath = File(context.filesDir, "ocr_results.txt").absolutePath
                    val externalFileName = "ocr_results.txt" // 또는 다른 이름을 사용할 수 있습니다.

                    moveFileToExternalStorage(internalFilePath, externalFileName) //외부저장소로 이동
                } else {
                    Log.e("httpE", "서버 응답 코드: ${response.code()}")
                    val errorBody = response.errorBody()?.string()
                    Log.e("httpE", "서버 오류: $errorBody")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("httpE", "이미지 서버 전송 실패: ${t.message}")
                Log.e("httpE","오류 상세 정보: ",t)
            }
        })
    }

    private fun parseOcrResults(jsonString: String): List<String> {
        val resultList = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val text = jsonObject.getString("text")
                resultList.add(text)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return resultList
    }

    private fun saveTextToFile(texts: List<String>, fileName: String) {
        try {
            val file = File(context.filesDir, fileName)
            file.printWriter().use { out ->
                texts.forEach { text ->
                    out.println(text)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveFileToExternalStorage(internalFilePath: String, externalFileName: String) {
        try {
            val internalFile = File(internalFilePath)
            val externalFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + externalFileName
            val externalFile = File(externalFilePath)

            internalFile.inputStream().use { inputStream ->
                externalFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: Exception) {
            Log.e("FileMoveError", "Error moving file: ${e.message}")
        }
    }
}