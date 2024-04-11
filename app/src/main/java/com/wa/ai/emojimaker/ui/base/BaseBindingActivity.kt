package com.wa.ai.emojimaker.ui.base

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.wa.ai.emojimaker.R
import com.wa.ai.emojimaker.ui.dialog.PermissionDialog
import com.wa.ai.emojimaker.utils.PermissionApp
import com.wa.ai.emojimaker.utils.extention.isWifiConnect

abstract class BaseBindingActivity<B : ViewDataBinding, VM : BaseViewModel> : BaseActivity() {
    lateinit var binding: B
    lateinit var viewModel: VM
    var permissionDialog: PermissionDialog? = null
    private var isDispatchTouchEvent = true

    abstract val layoutId: Int
    abstract fun getViewModel(): Class<VM>
    abstract fun setupView(savedInstanceState: Bundle?)
    abstract fun setupData()

    private fun Window.setLightStatusBars(b: Boolean) {
        WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        lockPortraitOrientation(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this)[getViewModel()]
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.setLightStatusBars(false)
        setupView(savedInstanceState)
        setupData()
    }


    override fun onStop() {
        super.onStop()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        toast?.cancel()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return !isDispatchTouchEvent || super.dispatchTouchEvent(ev)
    }

    private var toast: Toast? = null

    private var handlerToast = Handler(Looper.getMainLooper())
    fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()

        handlerToast.postDelayed({
            toast?.cancel()
        }, 1500)
    }


    override fun onDestroy() {
        super.onDestroy()
        handlerToast.removeCallbacksAndMessages(null)
    }

    fun setUpDialogPermission() {
        permissionDialog = PermissionDialog()
        permissionDialog?.clickOkPermission = {
            PermissionApp.gotoSettingRequestPermission(this)
        }
    }

    fun dismissDialogPermission() {
        permissionDialog?.apply {
            if (isVisible) {
                permissionDialog?.dismiss()
            }
        }
    }

    protected fun connectToWifi() {
        if (isWifiConnect()) {
            toast(getString(R.string.unable_to_access_the_internet_please_visit_connection_wifi_settings))
        } else {
            kotlin.runCatching {
                startActivity(Intent("android.settings.WIFI_DISPLAY_SETTINGS"))
            }.onFailure {
                startActivity(Intent("android.settings.CAST_SETTINGS"))
            }
        }
    }

    fun isDispatchTouchEvent(time: Int) {
        isDispatchTouchEvent = false
        Handler(Looper.getMainLooper()).postDelayed({ isDispatchTouchEvent = true }, time.toLong())
    }

    private fun lockPortraitOrientation(activity: Activity) {
        if (isSamsungDeviceBelowAndroid10()) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    private fun isSamsungDeviceBelowAndroid10(): Boolean {
        val manufacturer = Build.MANUFACTURER
        try {
            if (manufacturer.equals("Samsung", ignoreCase = true) && Build.VERSION.SDK_INT < 29) {
                return true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

}