package com.wa.ai.emojimaker.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.ai.emojimaker.App
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.common.Constant
import com.wa.ai.emojimaker.common.Constant.KEY_MEDIA
import com.wa.ai.emojimaker.common.Constant.WRITE_REQUEST_CODE
import com.wa.ai.emojimaker.databinding.ActivityMainBinding
import com.wa.ai.emojimaker.ui.base.BaseBindingActivity
import com.wa.ai.emojimaker.utils.DeviceUtils
import com.wa.ai.emojimaker.utils.RemoteConfigKey
import com.wa.ai.emojimaker.utils.ads.BannerUtils
import com.wa.ai.emojimaker.utils.extention.gone
import com.wa.ai.emojimaker.utils.extention.setFullScreen
import timber.log.Timber

class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun setupView(savedInstanceState: Bundle?) {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        setUpDialogPermission()
        setFullScreen()
        val toolbar: Toolbar = binding.toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController((navController))
    }

    override fun setupData() {

    }

    override fun onResume() {
        super.onResume()
        dismissDialogPermission()
    }

    override fun onStart() {
        super.onStart()
        loadBanner()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_REQUEST_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    permissionDialog?.show(supportFragmentManager, "permissionDialog")
                }
            }
        }
    }


    private fun loadBanner() {
        val keyAdsBanner: String
        val timeDelay: Long
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

        if (DeviceUtils.checkInternetConnection(this) && mFirebaseRemoteConfig.getBoolean(
                RemoteConfigKey.IS_SHOW_ADS_BANNER_MAIN)) {
            val adConfig = mFirebaseRemoteConfig.getString(RemoteConfigKey.KEY_ADS_BANNER_MAIN)
            keyAdsBanner = adConfig.ifEmpty {
                getString(R.string.banner_main)
            }

            val timeConfig = mFirebaseRemoteConfig.getLong(RemoteConfigKey.KEY_COLLAPSE_RELOAD_TIME)
            timeDelay = if (timeConfig == 0L) {
                20
            } else {
                timeConfig
            }
            BannerUtils.instance?.loadCollapsibleBanner(this, keyAdsBanner, timeDelay * 1000)
        } else {
            binding.rlBanner.gone()
        }
    }

}