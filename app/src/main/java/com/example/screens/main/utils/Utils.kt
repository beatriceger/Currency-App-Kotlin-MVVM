package com.example.screens.main.utils

import android.content.Context
import com.example.currencyapp.R


fun setCurrencyIcon(context: Context, currency: String): Int {
    var icon = context.resources.getIdentifier(
        "ic_$currency", "drawable", context.packageName
    )
    return if (icon != 0) {
        icon
    } else {
        R.drawable.ic_usd
    }
}