package com.felipesilveira.psafecryptotracker.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.felipesilveira.psafecryptotracker.R
import com.felipesilveira.psafecryptotracker.utils.App
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Felipe Silveira on 2/9/2018.
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //job scheduler
        App.scheduleBackgroundCacheJob(this)
    }
}
