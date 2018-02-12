package com.felipesilveira.psafecryptotracker.connection

import com.felipesilveira.psafecryptotracker.model.CryptoModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Felipe Silveira on 2/10/2018.
 */
interface ConnectionInterface {
    ////Get crypto list
    @GET("/v1/ticker")
    fun getTop10Crypto(@Query("convert") convert: String, @Query("limit") limit: String): Call<ArrayList<CryptoModel>>

}