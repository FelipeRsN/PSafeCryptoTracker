package com.felipesilveira.psafecryptotracker.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class CryptoModel(
        val price_usd: String?,
        val symbol: String?,
        val last_updated: String?,
        val total_supply: String?,
        val price_btc: String?,
        val available_supply: String?,
        val market_cap_usd: String?,
        val percent_change_1h: String?,
        val percent_change_24h: String?,
        val percent_change_7d: String?,
        val name: String?,
        val max_supply: String?,
        val rank: String?,
        val id: String?,
        val price_brl: String?
) : Parcelable



