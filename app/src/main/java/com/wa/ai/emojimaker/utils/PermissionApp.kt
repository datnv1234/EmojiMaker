package com.wa.ai.emojimaker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wa.ai.emojimaker.common.Constant

object PermissionApp {
    private val permissionsImage = arrayOf(
        "android.permission.READ_MEDIA_IMAGES"
    )
    private val permissionsVideo = arrayOf(
        "android.permission.READ_MEDIA_VIDEO"
    )
    private val permissionsMusic = arrayOf(
        "android.permission.READ_MEDIA_AUDIO"
    )
    private var storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

//    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
//    var storagePermissions_33 = arrayOf(
//        Manifest.permission.READ_MEDIA_IMAGES,
//        Manifest.permission.READ_MEDIA_VIDEO,
//        Manifest.permission.READ_MEDIA_AUDIO,
//    )

    private fun permissions(): Array<String> {
//        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            storagePermissions_33
//        } else {
//            storagePermissions
//        }
        return storagePermissions
    }

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            permissions(),
            Constant.WRITE_REQUEST_CODE
        )
    }
    fun checkRecordAudioPermission(activity: Activity): Boolean {
        return if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
            false
        } else {

            true
        }
    }

    fun Context.checkPermissionsReadExternal(): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED /*||result2==PackageManager.PERMISSION_GRANTED*/
        }


    }

    fun Activity.requestPermission(context: Activity) {
        ActivityCompat.requestPermissions(
            context,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constant.REQUEST_PERMISSION_CODE
        )
    }

    fun gotoSettingRequestPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    }

    fun checkPermissionsImage(context: Context?): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result1 =
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_MEDIA_IMAGES)
            result1 == PackageManager.PERMISSION_GRANTED
        } else if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result1 = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    fun checkPermissionsMusic(context: Context?): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result1 =
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_MEDIA_AUDIO)
            result1 == PackageManager.PERMISSION_GRANTED
        } else if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result1 = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    fun checkPermissionsVideo(context: Context?): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result1 =
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_MEDIA_VIDEO)
            result1 == PackageManager.PERMISSION_GRANTED
        } else if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result1 = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestPerMedia(activity: Activity, typePer: Int) {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (typePer == Constant.TYPE_PHOTO) {
                activity.requestPermissions(permissionsImage, Constant.REQUEST_MEDIA_IMAGE)
            } else if (typePer == Constant.TYPE_VIDEO) {
                activity.requestPermissions(permissionsVideo, Constant.REQUEST_MEDIA_VIDEO)
            } else if (typePer == Constant.TYPE_MUSIC) {
                activity.requestPermissions(permissionsMusic, Constant.REQUEST_MEDIA_MUSIC)
            }
        } else {
            if (typePer == Constant.TYPE_PHOTO) {
                activity.requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constant.REQUEST_PERMISSION_PHOTO
                )
            } else if (typePer == Constant.TYPE_VIDEO) {
                activity.requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constant.REQUEST_PERMISSION_VIDEO
                )
            } else if (typePer == Constant.TYPE_MUSIC) {
                activity.requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    Constant.REQUEST_PERMISSION_MUSIC
                )
            }
        }
    }
}