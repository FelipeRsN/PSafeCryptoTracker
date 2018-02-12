package com.felipesilveira.psafecryptotracker.utils

import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.net.ConnectivityManager
import com.felipesilveira.psafecryptotracker.connection.CacheJobScheduler
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Felipe Silveira on 2/10/2018.
 */

class App : Application(){

    companion object STATIC_ITENS {
        const val COIN_MARKET_CAP_BASE_URL = "https://api.coinmarketcap.com"
        const val CONNECTION_TIMEOUT: Long = 15
        const val SAVED_INSTANCE_CRYPTO_LIST = "savedInstance.cryptolist"
        const val SELECTED_CURRENCY = "BRL"
        const val NUMBER_OF_RESULTS = "10"
        const val JOB_SCHEDULER_MINIMUM_LATENCY: Long = 1000 * 60 * 15  //15 minutes
        const val JOB_SCHEDULER_MINIMUM_DEADLINE: Long = 1000 * 60 * 20 //20 minutes

        //Detected if network is available
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        //Schedule job to update cache in background
        fun scheduleBackgroundCacheJob(context: Context) {
            val builder = JobInfo.Builder(1, ComponentName(context.packageName, CacheJobScheduler::class.java.name))
            builder.setMinimumLatency(JOB_SCHEDULER_MINIMUM_LATENCY)
            builder.setOverrideDeadline(JOB_SCHEDULER_MINIMUM_DEADLINE)
            builder.setPersisted(true)
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)

            val mJobScheduler: JobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            mJobScheduler.schedule(builder.build())
        }

        fun getRetrofit(): Retrofit{
            val logging = HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY)

            val httpClient = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .readTimeout(App.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(App.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(App.CONNECTION_TIMEOUT, TimeUnit.SECONDS)

            return Retrofit.Builder()
                    .baseUrl(App.COIN_MARKET_CAP_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()

        }
    }
}