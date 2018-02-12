package com.felipesilveira.psafecryptotracker.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Felipe Silveira on 2/10/2018.
 */

class DBCore private constructor(ctx: Context) : SQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {

    companion object {
        private var db: DBCore? = null
        private val DB_NAME = "PSafeCryptoLocalBase"
        private val DB_VERSION = 1

        fun getInstance(ctx: Context): DBCore? {
            if (db == null) db = DBCore(ctx)
            return db
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table cryptoCache(cachedItems text, lastModifiedDate date)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table IF EXISTS cryptoCache;")
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table IF EXISTS cryptoCache;")
        onCreate(db)
    }
}