package com.felipesilveira.psafecryptotracker.item

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.felipesilveira.psafecryptotracker.R
import com.felipesilveira.psafecryptotracker.model.CryptoModel
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Created by Felipe Silveira on 2/9/2018.
 */
class CryptoCardListItem(cryptoModel: CryptoModel?, ctx: Context) : AbstractItem<CryptoCardListItem, CryptoCardListItem.ViewHolder>() {

    private val model: CryptoModel? = cryptoModel
    private val context: Context = ctx


    override fun getType(): Int {
        return R.id.list_item_crypto_card
    }

    override fun getLayoutRes(): Int {
        return R.layout.list_item_crypto
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)

        holder.cryptoRank.text = "#${model?.rank}"
        holder.cryptoName.text = " - ${model?.name}"
        holder.cryptoSymbol.text = model?.symbol

        val cryptoValue: Double? = model?.price_brl?.toDouble()

        holder.cryptoCurrentValue.text = "R$ ${round(cryptoValue, 2)}"

        checkPercentageValue(holder.percentage1h, model?.percent_change_1h?.toDouble())
        checkPercentageValue(holder.percentage24h, model?.percent_change_24h?.toDouble())
        checkPercentageValue(holder.percentage7d, model?.percent_change_7d?.toDouble())
    }

    private fun checkPercentageValue(textView: TextView, value: Double?){
        when{
            value != null -> {
                val decimalValue = String.format("%.2f", value)

                textView.text = "$decimalValue%"

                if(value > 0) textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                else textView.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
            }
            else -> {
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                textView.text = "0%"
            }
        }
    }

    private fun round(value: Double?, places: Int): Double {
        return when {
            value != null -> {
                var value = value
                if (places < 0) throw IllegalArgumentException()

                val factor = Math.pow(10.0, places.toDouble()).toLong()
                value *= factor
                val tmp = Math.round(value)
                tmp.toDouble() / factor
            }
            else -> 0.0
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cryptoName: TextView = view.findViewById(R.id.cryptoName)
        var cryptoRank: TextView = view.findViewById(R.id.cryptoRank)
        var cryptoSymbol: TextView = view.findViewById(R.id.cryptoSymbol)
        var cryptoCurrentValue: TextView = view.findViewById(R.id.cryptoCurrentValue)
        var percentage1h: TextView = view.findViewById(R.id.cryptoPercentage1h)
        var percentage24h: TextView = view.findViewById(R.id.cryptoPercentage24h)
        var percentage7d: TextView = view.findViewById(R.id.cryptoPercentage7d)
    }
}