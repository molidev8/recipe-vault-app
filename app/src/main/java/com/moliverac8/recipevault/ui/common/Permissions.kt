package com.moliverac8.recipevault.ui.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {

    private val PERMISSIONS = listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION)
    const val PERMISSION_CODE = 1

    fun hasPermissions(context: Context): Boolean = PERMISSIONS.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissionsFragment(request: (Array<String>, Int) -> Unit) =
        request(PERMISSIONS.toTypedArray(), PERMISSION_CODE)


    fun requestPermissionActivity(context: Activity) =
        ActivityCompat.requestPermissions(context, PERMISSIONS.toTypedArray(), PERMISSION_CODE)

}
