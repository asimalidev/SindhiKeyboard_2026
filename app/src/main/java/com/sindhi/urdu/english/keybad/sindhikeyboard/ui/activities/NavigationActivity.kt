package com.sindhi.urdu.english.keybad.sindhikeyboard.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.manual.mediation.library.sotadlib.activities.AppCompatBaseActivity
import com.manual.mediation.library.sotadlib.utilsGoogleAdsConsent.GoogleMobileAdsConsentManager
import com.sindhi.urdu.english.keybad.R
import com.sindhi.urdu.english.keybad.databinding.ActivityNavigationBinding
import com.sindhi.urdu.english.keybad.sindhikeyboard.utils.*
import androidx.activity.result.contract.ActivityResultContracts
import com.sindhi.urdu.english.keybad.sindhikeyboard.utils.PURCHASE

class NavigationActivity : AppCompatBaseActivity() {
    private lateinit var binding: ActivityNavigationBinding
    private lateinit var navController: NavController
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private var isPurchased: Boolean? = null
    private lateinit var appUpdateManager: AppUpdateManager

    private val updateActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.e(" ", "Update flow failed! Result code: ${result.resultCode}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.inclToolBar.clSubDiv.let { subDiv ->
            ViewCompat.setOnApplyWindowInsetsListener(subDiv) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, systemBars.top, 0, 0)
                insets
            }
        }

        // Check if main exists before setting listener
        binding.main.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
                insets
            }
        }


        // Initialize consent + purchase
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
        isPurchased = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PURCHASE, false)

        // Set navigation graph start destination
        navController = findNavController(R.id.nav_host_fragment_content_navigation)
        val navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        val moveTo = intent.getStringExtra("MoveTo")
        navGraph.setStartDestination(
            when (moveTo) {
                "Settings" -> R.id.settingsFragment
                "Themes" -> R.id.themesFragment
                else -> R.id.nav_home
            }
        )
        navController.graph = navGraph

        // ✅ Check for updates
        checkForUpdates()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.enabledInputMethodList.toString().contains(packageName)) {
                if (checkIfKeyboardEnabled()) {
                    if (!isInputMethodEnabled()) {
                        startActivity(Intent(this@NavigationActivity, KeyboardSelectionActivity::class.java))
                    }
                }
            }
        }
    }

    // ✅ Modern Play Core 2.1.0 update check
    private fun checkForUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Listener for download completion
        val listener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
            }
        }
        appUpdateManager.registerListener(listener)

        // Fetch update info
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateActivityResultLauncher, // ✅ use launcher instead of "this"
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                            .setAllowAssetPackDeletion(true)
                            .build()
                    )
                } catch (e: Exception) {
                    Log.e("AppUpdate", "startUpdateFlowForResult failed", e)
                }
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("RESTART") {
            appUpdateManager.completeUpdate()
        }.show()
    }
}





