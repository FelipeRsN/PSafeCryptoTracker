package com.felipesilveira.psafecryptotracker.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.felipesilveira.psafecryptotracker.sqlite.DBCore
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.util.*


/**
 * Created by Felipe Silveira on 2/10/2018.
 */
class DBLiteConnection private constructor(context: Context) {
    private val db: SQLiteDatabase?

    companion object {
        private var instance: DBLiteConnection? = null

        fun getInstance(context: Context): DBLiteConnection? {
            if (instance == null) instance = DBLiteConnection(context)
            return instance
        }
    }

    init {
        val dbcore = DBCore.getInstance(context)
        db = dbcore?.writableDatabase
    }

    /////////////////////////////////////////////////////////////
    // select
    val getCachedCryptoList: ArrayList<CryptoModel>?
        get() {
            val columns = arrayOf("cachedItems")
            val cursor = db?.query("cryptoCache", columns, null, null, null, null, null)
            return if (cursor != null && cursor.moveToFirst()) {
                val items = cursor.getString(0)
                val jsonArray = JSONArray(items)

                //Return String to ArrayList using Gson
                val gson = GsonBuilder().create()
                val playersList = gson.fromJson(jsonArray.toString(), object : TypeToken<ArrayList<CryptoModel>>(){}.type) as ArrayList<CryptoModel>

                playersList
            } else null
        }

    val getLastModifiedDate: Date?
        get() {
            val columns = arrayOf("lastModifiedDate")
            val cursor = db?.query("cryptoCache", columns, null, null, null, null, null)
            return if(cursor != null && cursor.moveToFirst()){
                val time = cursor.getString(0)
                Date(time.toLong())
            }else null
        }

    val hasItemCached: Boolean
        get() {
            val columns = arrayOf("cachedItems")
            val cursor = db?.query("cryptoCache", columns, null, null, null, null, null)
            return cursor != null && cursor.moveToFirst()
        }

    /////////////////////////////////////////////////////////////
    // insert
    fun insertListToCache(cachedItems: ArrayList<CryptoModel>?, lastModifiedDate: Date) {
        //Convert crypto arrayList to JsonArray using Gson and save into sqlite as String
        val gson = GsonBuilder().create()
        val myCustomArray = gson.toJsonTree(cachedItems).asJsonArray

        val values = ContentValues()
        values.put("cachedItems", myCustomArray.toString())
        values.put("lastModifiedDate", lastModifiedDate.time.toString())
        db?.insert("cryptoCache", null, values)
    }

    /////////////////////////////////////////////////////////////
    // update
    fun updateCachedItems(cachedItems: ArrayList<CryptoModel>?, lastModifiedDate: Date) {
        //Convert crypto arrayList to JsonArray using Gson and save into sqlite as String
        val gson = GsonBuilder().create()
        val myCustomArray = gson.toJsonTree(cachedItems).asJsonArray

        val values = ContentValues()
        values.put("cachedItems", myCustomArray.toString())
        values.put("lastModifiedDate", lastModifiedDate.time.toString())
        db?.update("cryptoCache", values, null, null)
    }

    /////////////////////////////////////////////////////////////
    // delete
    fun clearCryptoCache() {
        db?.delete("cryptoCache", null, null)
    }
}