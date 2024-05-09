package com.dreamworks.offlinereading.utils

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit


object RetrofitRxJavaExample {


    //fact
    internal interface ApiService {
        @GET("users/{user}/starred")
        fun getWeather(@Path("user") user: String): Observable<List<UserResponse>?>?
    }

    internal interface WeatherService {
        @GET("fact")
        fun getWeather(): Observable<WeatherResponse>?
    }


    @JvmStatic
    fun main(args: Array<String>) {


//        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
//        val stringRequest = StringRequest(
//            Request.Method.GET,
//            "url",
//            {
//
//            },
//            {
//
//            }
//        )
//        requestQueue.add(stringRequest)


//        val user = readln()

//        val myDataBase: SQLiteDatabase
//        val mPath: String = "ll"
//        myDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READWRITE)
//
//        val values = ContentValues()
//        values.put("First_Name", "str_edtfname")
//        values.put("Last_Name", "str_edtlname")
//        myDataBase.insert("table_name", null, values)

        // Create Retrofit instance
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        //
        //https://api.github.com/
        val retrofit = Retrofit.Builder()
            .baseUrl("https://catfact.ninja/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        // Create an instance of the API service


        val dataList: List<String> =
            arrayListOf("","","","","","","","","")// populate your list here
        // Coroutine scope to launch concurrent requests
        runBlocking {
            val jobs = dataList.map { data ->
                // Launch a coroutine for each API call
                launch {
                    try {
                        val weatherService: WeatherService? = retrofit.create(WeatherService::class.java)
                        val wereq = weatherService!!.getWeather()

                            ?.subscribe({ weather ->
                                // Handle the response

                                println("Fact: " + weather.fact)



                            }) { throwable ->
                                // Handle errors
                                System.err.println("Error occurred: " + throwable.message)
                            }
                        wereq!!.dispose()
                        // Handle success response
                    } catch (e: Exception) {
                        // Handle failure
                    }
                }
            }

            // Wait for all jobs to complete
            jobs.joinAll()
        }


//        val apiService: ApiService? = retrofit.create(ApiService::class.java)
//        val req = apiService!!.getWeather(user)
//
//            ?.subscribe({ weather ->
//                // Handle the response
//                weather!!.map { data ->
//                    println("Fact: " + data.name)
//                }
//
//
//            }) { throwable ->
//                // Handle errors
//                System.err.println("Error occurred: " + throwable.message)
//            }
//        req!!.dispose()





        // Make an API call using RxJava

        // Subscribe to the observable


//        try {
//            Thread.sleep(5000) // Sleep for 5 seconds
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
    }

    // Define the interface for the API endpoints

    // Define a model class for the response
    data class WeatherResponse(
        val fact: String,
        val length: Int,
    )

    data class UserResponse(
        val id: Int,
        val name: String,
        val htmlUrl: String,
        val description: String,
        val language: String,
        val stargazersCount: Int,
    )

    private fun getOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient
            .Builder()
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }


}