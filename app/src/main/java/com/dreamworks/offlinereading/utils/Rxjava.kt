package com.dreamworks.offlinereading.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import io.reactivex.Observable
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


object RetrofitRxJavaExample {

    @SuppressLint("CheckResult")
    @JvmStatic
    fun main(args: Array<String>) {


//        val myDataBase: SQLiteDatabase
//        val mPath: String = "ll"
//        myDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE)
//
//        val values = ContentValues()
//        values.put("First_Name", "str_edtfname")
//        values.put("Last_Name", "str_edtlname")
//        myDataBase.insert("table_name", null, values)

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://catfact.ninja/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        // Create an instance of the API service


        val dataList: List<String> =
            arrayListOf("", "", "", "", "", "", "", "")// populate your list here
        var apiService: ApiService? = null
// Coroutine scope to launch concurrent requests
        runBlocking {
            val jobs = dataList.map { data ->
                // Launch a coroutine for each API call
                launch {
                    try {
                        apiService = retrofit.create(ApiService::class.java)
                        apiService!!.getWeather()?.subscribe({ weather ->
                            // Handle the response
                            println("Fact: " + weather!!.fact)

                        }) { throwable ->
                            // Handle errors
                            System.err.println("Error occurred: " + throwable.message)
                        }
                        // Handle success response
                    } catch (e: Exception) {
                        // Handle failure
                    }
                }
            }

            // Wait for all jobs to complete
            jobs.joinAll()
        }


        // Make an API call using RxJava

        // Subscribe to the observable


//        try {
//            Thread.sleep(5000) // Sleep for 5 seconds
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
    }

    // Define the interface for the API endpoints
    internal interface ApiService {
        @GET("fact")
        fun getWeather(): Observable<WeatherResponse?>?
    }

    // Define a model class for the response
    data class WeatherResponse(
        val fact: String,
        val length: Int,


        )
}