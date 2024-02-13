package com.dreamworks.offlinereading.hiltApp


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()


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

    @Singleton
    @Provides
    @Named("retrofit")
    fun provideRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(getOkHttpClient())
            .build()
    }

//    @Singleton
//    @Provides
//    fun provideRetrofitService(@Named("retrofit") retrofit: Retrofit):LoginService{
//        return retrofit.create(LoginService::class.java)
//    }
//    @Singleton
//    @Provides
//    fun provideLoginRepository(retrofitService: LoginService):LoginRepo{
//        return LoginRepo(retrofitService)
//    }
//
//    @Singleton
//    @Provides
//    fun providePmsRetrofitService(@Named("retrofit") retrofit: Retrofit):PmsService{
//        return retrofit.create(PmsService::class.java)
//    }
//
//    @Singleton
//    @Provides
//    fun providePmsRepository(retrofitService: PmsService):PmsRepo{
//        return PmsRepo(retrofitService)
//    }
}