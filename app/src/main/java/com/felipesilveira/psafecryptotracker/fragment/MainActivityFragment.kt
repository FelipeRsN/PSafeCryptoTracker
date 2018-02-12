package com.felipesilveira.psafecryptotracker.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.felipesilveira.psafecryptotracker.R
import com.felipesilveira.psafecryptotracker.connection.ConnectionInterface
import com.felipesilveira.psafecryptotracker.item.CryptoCardListHeader
import com.felipesilveira.psafecryptotracker.item.CryptoCardListItem
import com.felipesilveira.psafecryptotracker.model.CryptoModel
import com.felipesilveira.psafecryptotracker.model.DBLiteConnection
import com.felipesilveira.psafecryptotracker.utils.App
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

/**
 * Created by Felipe Silveira on 2/9/2018.
 */

class MainActivityFragment : Fragment() {

    //FastAdapter
    private var fastItemAdapter = FastItemAdapter<IItem<*, *>>()
    private var headerItemAdapter = ItemAdapter<IItem<*, *>>()

    //Retrofit / OkHttp
    private lateinit var retrofit: Retrofit


    private var dbl: DBLiteConnection? = null
    private var cryptoList: ArrayList<CryptoModel>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(App.SAVED_INSTANCE_CRYPTO_LIST))
                cryptoList = savedInstanceState.getParcelableArrayList(App.SAVED_INSTANCE_CRYPTO_LIST)
        }

        initVariablesAndSetupListeners()
        setupRecyclerViewAndGetData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(cryptoList != null){
            outState.putParcelableArrayList(App.SAVED_INSTANCE_CRYPTO_LIST, cryptoList!!)
        }
    }

    //Initialize variables and set RefreshListener to swipeToRefresh
    private fun initVariablesAndSetupListeners(){
        retrofit = App.getRetrofit()

        if(context != null) dbl = DBLiteConnection.getInstance(context!!)

        swipeToRefresh.setOnRefreshListener {
            getTop10Crypto() }
    }

    //Setup recyclerView layoutManager and set adapter using FastAdapter lib
    private fun setupRecyclerViewAndGetData(){
        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        else
            recyclerView.layoutManager = GridLayoutManager(context, 2)

        //Add header to adapter
        fastItemAdapter.addAdapter(0, headerItemAdapter)

        fastItemAdapter.setHasStableIds(true)
        recyclerView.adapter = fastItemAdapter

        verifyAndGetData()
    }


    //Verify onSavedInstance list and cache to obtain crypto list
    private fun verifyAndGetData(){
        //Check if cryptoList different of null (onSavedInstance) to populate recyclerView
        if(cryptoList != null){
            addItemsToAdapter(cryptoList!!)
        }else{
            //Detect if has item on cache
            if(dbl != null && dbl!!.hasItemCached){
                val cachedItems = dbl!!.getCachedCryptoList
                if(cachedItems != null){
                    addItemsToAdapter(cachedItems)
                }
            }else getTop10Crypto()
        }
    }

    private fun showSwipeRefresh(){
        if(!swipeToRefresh.isRefreshing) swipeToRefresh.isRefreshing = true
    }

    private fun dismissSwipeRefresh(){
        if(swipeToRefresh.isRefreshing) swipeToRefresh.isRefreshing = false
    }

    private fun addItemsToAdapter(list: ArrayList<CryptoModel>?){
        if(context != null) {
            fastItemAdapter.clear()

            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) fastItemAdapter.add(CryptoCardListHeader(context!!))

            list?.forEach {
                fastItemAdapter.add(CryptoCardListItem(it, context!!))
            }
        }
    }

    private fun getTop10Crypto(){
        //Check internet connection to continue
        if(context != null && App.isNetworkAvailable(context!!)) {

            showSwipeRefresh()

            val service = retrofit.create(ConnectionInterface::class.java)
            val call = service.getTop10Crypto(App.SELECTED_CURRENCY, App.NUMBER_OF_RESULTS)

            call.enqueue(object : Callback<ArrayList<CryptoModel>> {
                override fun onResponse(call: Call<ArrayList<CryptoModel>>, response: Response<ArrayList<CryptoModel>>) {
                    Log.d("onResponse", response.toString())

                    dismissSwipeRefresh()

                    if (response.isSuccessful) {
                        Log.d("onResponse", response.body().toString())

                        cryptoList = response.body()

                        //Clear cache and save new list
                        if(dbl != null){
                            if(dbl!!.hasItemCached){
                                dbl!!.updateCachedItems(cryptoList, Date())
                            }else{
                                dbl!!.insertListToCache(cryptoList, Date())
                            }
                        }

                        addItemsToAdapter(cryptoList)
                    } else {
                        Snackbar.make(activity!!.findViewById(android.R.id.content), response.message(), Snackbar.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ArrayList<CryptoModel>>, t: Throwable) {
                    Log.d("onFailure", t.toString())
                    dismissSwipeRefresh()
                    Snackbar.make(activity!!.findViewById(android.R.id.content), t.toString(), Snackbar.LENGTH_LONG).show()
                }
            })

        }else{
            dismissSwipeRefresh()
            Snackbar.make(activity!!.findViewById(android.R.id.content), getString(R.string.noInternetDetected), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.tryAgain), { getTop10Crypto() }).show()
        }

    }

}
