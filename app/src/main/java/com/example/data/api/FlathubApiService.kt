package com.example.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface FlathubApiService {

    @GET("collection/trending")
    suspend fun getTrending(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24
    ): CollectionResponse

    @GET("collection/popular")
    suspend fun getPopular(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24
    ): CollectionResponse

    @GET("collection/recently-added")
    suspend fun getRecentlyAdded(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24
    ): CollectionResponse

    @GET("collection/recently-updated")
    suspend fun getRecentlyUpdated(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 24
    ): CollectionResponse

    @GET("collection/category/{category}")
    suspend fun getCategoryApps(
        @Path("category") category: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 12
    ): CollectionResponse

    @GET("app-picks/apps-of-the-week/{date}")
    suspend fun getAppsOfTheWeek(
        @Path("date") date: String
    ): AppPickWeeklyResponse

    @GET("appstream/{app_id}")
    suspend fun getAppstream(
        @Path("app_id") appId: String
    ): AppstreamDetail

    @POST("search")
    suspend fun searchApps(
        @Body request: SearchRequest
    ): SearchResponse

    companion object {
        private const val BASE_URL = "https://flathub.org/api/v2/"

        fun create(): FlathubApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(FlathubApiService::class.java)
        }
    }
}
