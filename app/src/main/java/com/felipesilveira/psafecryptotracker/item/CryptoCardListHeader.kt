package com.felipesilveira.psafecryptotracker.item

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.felipesilveira.psafecryptotracker.R
import com.felipesilveira.psafecryptotracker.model.DBLiteConnection
import com.mikepenz.fastadapter.items.AbstractItem
import java.text.SimpleDateFormat

/**
 * Created by Felipe Silveira on 2/9/2018.
 */
class CryptoCardListHeader(ctx: Context) : AbstractItem<CryptoCardListHeader, CryptoCardListHeader.ViewHolder>() {

    private val context: Context = ctx


    override fun getType(): Int {
        return R.id.list_item_crypto_card_header
    }

    override fun getLayoutRes(): Int {
        return R.layout.list_item_crypto_header
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        val dbl = DBLiteConnection.getInstance(context)

        if(dbl != null && dbl.hasItemCached){
            val date = dbl.getLastModifiedDate

            val localDateFormat = SimpleDateFormat("HH:mm")
            val time = localDateFormat.format(date)

            holder.lastUpdateAt.text = "${context.getString(R.string.lastUpdateAt)} $time"
        }else{
            holder.lastUpdateAt.visibility = View.GONE
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var lastUpdateAt: TextView = view.findViewById(R.id.lastUpdateAt)
    }
}