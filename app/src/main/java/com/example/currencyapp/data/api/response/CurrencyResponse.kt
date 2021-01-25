package com.example.currencyapp.data.api.response

import com.google.gson.annotations.SerializedName


data class RateResponse(
    @SerializedName("baseCurrency")
    var baseCurrency: String? = "",

    @SerializedName("rates")
    var rates: Map<String, Double> = HashMap()
)

data class RateDetailed(
    var icon: Int? = null,
    var rateName: String? = "",
    var rateDescription: String? = "",
    var rateMultiplier: Double = 0.0,
    var result: String = "",
    var position: Int = 0
)