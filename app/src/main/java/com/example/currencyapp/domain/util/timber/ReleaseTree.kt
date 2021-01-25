package com.example.currencyapp.domain.util.timber

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return priority == Log.ERROR || priority == Log.WARN
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
    }
}