package com.sindhi.urdu.english.keybad.sindhikeyboard.utils

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.sindhi.urdu.english.keybad.sindhikeyboard.ads.ApplicationClass.Companion.applicationClass

object FirebaseLog {
    private var analytics: FirebaseAnalytics? = null

    fun getAnalytics(context: Context): FirebaseAnalytics {
        return analytics ?: synchronized(this) {
            val instance = FirebaseAnalytics.getInstance(context.applicationContext)
            analytics = instance
            instance
        }
    }
}