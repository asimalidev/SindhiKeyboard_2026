package com.sindhi.urdu.english.keybad.sindhikeyboard.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.manual.mediation.library.sotadlib.utils.AdLoadingDialog
import com.manual.mediation.library.sotadlib.utils.NetworkCheck
import com.sindhi.urdu.english.keybad.R

//class ResumeAd(private val myApplicationClass: ApplicationClass) : Application.ActivityLifecycleCallbacks, LifecycleObserver {
//    private var adVisible = false
//    var appOpenAd: AppOpenAd? = null
//    private var currentActivity: Activity? = null
//    var isShowingDialog = true
//    var isShowingAd = false
//    private var fullScreenContentCallback: FullScreenContentCallback? = null
//
//    init {
//        // Register instantly
//        myApplicationClass.registerActivityLifecycleCallbacks(this)
//        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
//    }
//
//    fun fetchAd() {
//        if (isAdAvailable() || !NetworkCheck.isNetworkAvailable(myApplicationClass)) {
//            return
//        }
//
//        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
//            override fun onAdLoaded(ad: AppOpenAd) {
//                appOpenAd = ad
//                if (myApplicationClass.getString(R.string.ShowPopups) == "true") {
//                    Toast.makeText(myApplicationClass, "OpenAd :: AdMob :: Loaded", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onAdFailedToLoad(p0: LoadAdError) {
//                super.onAdFailedToLoad(p0)
//                if (myApplicationClass.getString(R.string.ShowPopups) == "true") {
//                    Toast.makeText(myApplicationClass, "OpenAd :: AdMob :: Failed to Load", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        val request: AdRequest = getAdRequest()
//        AppOpenAd.load(
//            myApplicationClass,
//            myApplicationClass.getString(R.string.admob_app_open),
//            request,
//            loadCallback
//        )
//
//        if (myApplicationClass.getString(R.string.ShowPopups) == "true") {
//            Toast.makeText(myApplicationClass, "OpenAd :: AdMob :: Request", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun showAdIfAvailable(onAdNotAvailableOrShown: (() -> Unit)? = null) {
//        if (!isShowingAd && isAdAvailable()) {
//            fullScreenContentCallback = object : FullScreenContentCallback() {
//                override fun onAdDismissedFullScreenContent() {
//                    isShowingDialog = false
//                    dismissWaitDialog()
//                    onAdNotAvailableOrShown?.invoke()
//                    appOpenAd = null
//                    isShowingAd = false
//                    adVisible = false
//                    fetchAd()
//                }
//
//                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
//                    isShowingDialog = false
//                    dismissWaitDialog()
//                    onAdNotAvailableOrShown?.invoke()
//                    if (myApplicationClass.getString(R.string.ShowPopups) == "true") {
//                        Toast.makeText(myApplicationClass, "OpenAd :: AdMob :: onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onAdShowedFullScreenContent() {
//                    isShowingAd = true
//                    isShowingDialog = false
//                    dismissWaitDialog()
//                }
//            }
//            adVisible = true
//            appOpenAd?.fullScreenContentCallback = fullScreenContentCallback
//            isShowingDialog = true
//            showWaitDialog()
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                currentActivity?.let { appOpenAd?.show(it) }
//                dismissWaitDialog()
//            }, 1500)
//
//        } else {
//            isShowingDialog = false
//            dismissWaitDialog()
//            onAdNotAvailableOrShown?.invoke()
//
//            // Replaced redundant `.equals("")` check with standard Kotlin null/empty check
//            if (!currentActivity?.localClassName.isNullOrEmpty()) {
//                fetchAd()
//            }
//        }
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onAppForegrounded() {
//        val activity = currentActivity ?: return
//        val localClassName = activity.localClassName ?: return
//
//        // Check preferences using the Application context to utilize cached SharedPreferences
//        // instead of hitting the disk repeatedly with the Activity context
//        val pref = myApplicationClass.getSharedPreferences("REMOTE_CONFIG", Context.MODE_PRIVATE)
//        val remoteConfigPref = myApplicationClass.getSharedPreferences("RemoteConfig", Context.MODE_PRIVATE)
//
//        val isRestrictedActivity = localClassName in listOf(
//            "activities.SOTSplash",
//            "com.manual.mediation.library.sotadlib.activities.LanguageScreenOne",
//            "com.manual.mediation.library.sotadlib.activities.WalkThroughConfigActivity",
//            "com.manual.mediation.library.sotadlib.activities.LanguageScreenDup",
//            "com.manual.mediation.library.sotadlib.activities.WelcomeScreenOne",
//            "com.manual.mediation.library.sotadlib.activities.WelcomeScreenDup",
//            "com.google.android.gms.ads_cleaner.AdActivity"
//        ) || activity.componentName.className in listOf(
//            "com.sindhi.urdu.english.keybad.sindhikeyboard.ui.activities.SelectKeyboardActivity",
//            "com.sindhi.urdu.english.keybad.sindhikeyboard.ui.activities.FOFStartActivity"
//        )
//
//        if (!isRestrictedActivity && remoteConfigPref.getBoolean("RESUME_OVERALL", true)) {
//            if (!InterstitialClassAdMob.isInterstitialAdVisible && !pref.getBoolean("IS_PURCHASED", false)) {
//                Log.e("Unique", localClassName)
//                showAdIfAvailable()
//            }
//        }
//    }
//
//    private fun getAdRequest(): AdRequest {
//        return AdRequest.Builder().build()
//    }
//
//    private fun isAdAvailable(): Boolean {
//        return appOpenAd != null
//    }
//
//    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
//
//    override fun onActivityStarted(p0: Activity) {
//        currentActivity = p0
//    }
//
//    override fun onActivityResumed(p0: Activity) {
//        currentActivity = p0
//    }
//
//    override fun onActivityPaused(p0: Activity) {
//        dismissWaitDialog()
//    }
//
//    override fun onActivityStopped(p0: Activity) {
//        dismissWaitDialog()
//    }
//
//    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
//
//    override fun onActivityDestroyed(p0: Activity) {
//        dismissWaitDialog()
//    }
//
//    private fun showWaitDialog() {
//        if (isShowingDialog) {
//            currentActivity?.let { activity ->
//                if (!activity.isFinishing) {
//                    AdLoadingDialog.dismissDialog(activity)
//
//                    val view = activity.layoutInflater.inflate(
//                        com.manual.mediation.library.sotadlib.R.layout.dialog_adloading,
//                        null,
//                        false
//                    )
//                    AdLoadingDialog.setContentView(
//                        activity,
//                        view = view,
//                        isCancelable = false
//                    ).showDialogInterstitial()
//                }
//            }
//        }
//    }
//
//    private fun dismissWaitDialog() {
//        currentActivity?.let { activity ->
//            if (!activity.isFinishing) {
//                AdLoadingDialog.dismissDialog(activity)
//            }
//        }
//    }
//}

class ResumeAd(globalClass: ApplicationClass? = null) : Application.ActivityLifecycleCallbacks,
    LifecycleObserver {

    companion object {
        var isShowingDialog = false
        var isShowingAd = false
        var isAdLoadInProgress = false
        var isLoadTimedOut = false
        var lastRequestTime: Long = 0 // Prevents rapid-fire requests
    }

    private var currentActivity: Activity? = null
    private val handler = Handler(Looper.getMainLooper())
    private var timeoutRunnable: Runnable? = null
    private var myApplicationClass: ApplicationClass? = globalClass

    init {
        myApplicationClass?.let {
            it.registerActivityLifecycleCallbacks(this)
            ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        val activity = currentActivity ?: return
        val className = activity.localClassName ?: ""

        Log.d("ResumeAd", "App Foregrounded in activity: $className")

        // 1. Interstitial Check
        if (InterstitialClassAdMob.isInterstitialAdVisible) {
            Log.d("ResumeAd", "Skipping Resume Ad: Interstitial is active.")
            return
        }

        // 2. GLOBAL STATE CHECK: Don't trigger if ANY instance is already loading/showing
        if (isShowingAd || isShowingDialog || isAdLoadInProgress) {
            Log.d("ResumeAd", "Skipping: Resume Ad already showing/loading in another instance.")
            return
        }

        // 3. DEBOUNCE CHECK: Prevent multiple calls within 3 seconds
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRequestTime < 3000) {
            Log.d("ResumeAd", "Skipping: Another ad was requested less than 3 seconds ago.")
            return
        }

        // 4. EXCLUSION LIST
        val excludedActivities = listOf(
            "ui.activities.SplashActivity",
            "ui.activities.KeyboardSelectionActivity",
            "com.manual.mediation.library.sotadlib.activities.LanguageScreenOne",
            "com.manual.mediation.library.sotadlib.activities.WalkThroughConfigActivity",
            "com.manual.mediation.library.sotadlib.activities.LanguageScreenDup",
            "com.manual.mediation.library.sotadlib.activities.WelcomeScreenOne",
            "com.manual.mediation.library.sotadlib.activities.WelcomeScreenDup",
            "com.applovin.adview.AppLovinFullscreenActivity",
            "com.applovin.adview.AppLovinInterstitialActivity",
            "com.facebook.ads.AudienceNetworkActivity",
            "com.facebook.ads.internal.ipc.RemoteANActivity",
            "com.bytedance.sdk.openadsdk",
            "com.mbridge.msdk",
            "com.google.android.gms.ads.AdActivity"
        )

        if (excludedActivities.any { className.contains(it) }) {
            Log.d("ResumeAd", "Skipping: Activity $className is in exclusion list.")
            return
        }

        // 5. REMOTE CONFIG & NETWORK
        val prefs = activity.getSharedPreferences("RemoteConfig", Context.MODE_PRIVATE)
        val isResumeEnabled = prefs?.getBoolean("RESUME_OVERALL", true) ?: true

        if (isResumeEnabled && com.manual.mediation.library.sotadlib.utils.NetworkCheck.isNetworkAvailable(
                activity
            )
        ) {
            Log.d("ResumeAd", "All checks passed. Requesting Resume Ad.")
            lastRequestTime = System.currentTimeMillis() // Update the debounce timer
            loadAndShowAd()
        }
    }

    private fun loadAndShowAd() {
        if (myApplicationClass != null && !NetworkCheck.isNetworkAvailable(myApplicationClass) && currentActivity?.localClassName.equals(
                "ui.activities.SplashActivity"
            )
        ) {
            return
        }

        isAdLoadInProgress = true
        isLoadTimedOut = false
        showWaitDialog()

        timeoutRunnable = Runnable {
            Log.d("ResumeAd", "Ad load timed out. Dismissing dialog.")
            isLoadTimedOut = true
            isAdLoadInProgress = false
            dismissWaitDialog()
        }
        handler.postDelayed(timeoutRunnable!!, 5000) // 5 seconds

        val loadCallback = object : AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                handler.removeCallbacks(timeoutRunnable!!)
                isAdLoadInProgress = false

                if (isLoadTimedOut) {
                    Log.d("ResumeAd", "Ad loaded late (after 5s timeout). Discarding ad.")
                    return
                }

                showAdNow(ad)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                handler.removeCallbacks(timeoutRunnable!!)
                isAdLoadInProgress = false
                dismissWaitDialog()
            }
        }

        val request: AdRequest = AdRequest.Builder().build()
        val pref = currentActivity?.getSharedPreferences("RemoteConfig", Context.MODE_PRIVATE)

        // Ensure you replace "AD_ID_RESUME_OVERALL" with your actual string literal if it's missing here
        val adId = pref?.getString(
            "RESUME_OVER_ALL",
            currentActivity?.resources?.getString(R.string.RESUME_OVER_ALL)
        )

        myApplicationClass?.applicationContext?.let {
            AppOpenAd.load(it, adId!!, request, loadCallback)
        }
    }

    private fun showAdNow(ad: AppOpenAd) {
        val fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isShowingAd = false
                isShowingDialog = false
                dismissWaitDialog()
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                isShowingAd = false
                isShowingDialog = false
                dismissWaitDialog()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
                dismissWaitDialog()
            }
        }

        ad.fullScreenContentCallback = fullScreenContentCallback

        currentActivity?.let { activity ->
            if (!activity.isFinishing) {
                ad.show(activity)
            } else {
                dismissWaitDialog()
                isShowingAd = false // Reset just in case
            }
        }
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}

    override fun onActivityStarted(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = p0
    }

    override fun onActivityPaused(p0: Activity) {}

    override fun onActivityStopped(p0: Activity) {}

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}

    override fun onActivityDestroyed(p0: Activity) {
        dismissWaitDialog()
    }

    private fun showWaitDialog() {
        if (isShowingDialog) return

        currentActivity?.let { act ->
            if (!act.isFinishing) {
                isShowingDialog = true
                val view = act.layoutInflater.inflate(
                    com.manual.mediation.library.sotadlib.R.layout.dialog_adloading,
                    null,
                    false
                )
                AdLoadingDialog.setContentView(act, view = view, isCancelable = false)
                    .showDialogInterstitial()
            }
        }
    }

    private fun dismissWaitDialog() {
        isShowingDialog = false
        currentActivity?.let { act ->
            if (!act.isFinishing && !act.isDestroyed) {
                try {
                    AdLoadingDialog.dismissDialog(act)
                } catch (e: Exception) {
                    Log.e("ResumeAd", "Failed to dismiss dialog: ${e.message}")
                }
            }
        }
    }
}