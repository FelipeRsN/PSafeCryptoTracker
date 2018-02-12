package com.felipesilveira.psafecryptotracker.connection

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.felipesilveira.psafecryptotracker.model.CryptoModel
import com.felipesilveira.psafecryptotracker.model.DBLiteConnection
import com.felipesilveira.psafecryptotracker.utils.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


/**
 * Created by Felipe Silveira on 2/12/2018.
 */

class CacheJobScheduler : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        val service = App.getRetrofit().create(ConnectionInterface::class.java)
        val call = service.getTop10Crypto(App.SELECTED_CURRENCY, App.NUMBER_OF_RESULTS)

        call.enqueue(object : Callback<ArrayList<CryptoModel>> {
            override fun onResponse(call: Call<ArrayList<CryptoModel>>, response: Response<ArrayList<CryptoModel>>) {
                Log.d("onResponse", response.toString())
                if (response.isSuccessful) {

                    val dbl = DBLiteConnection.getInstance(applicationContext)

                    //Clear cache and save new list
                    if(dbl != null){
                        if(dbl.hasItemCached){
                            dbl.updateCachedItems(response.body(), Date())
                        }else{
                            dbl.insertListToCache(response.body(), Date())
                        }
                    }
                    Log.d("JOB","JOB EXECUTED AND RECREATED")
                    jobFinished(params,false)
                    App.scheduleBackgroundCacheJob(baseContext)
                }
            }

            override fun onFailure(call: Call<ArrayList<CryptoModel>>, t: Throwable) {
                Log.d("onFailure", t.toString())
            }
        })
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return true
    }



}