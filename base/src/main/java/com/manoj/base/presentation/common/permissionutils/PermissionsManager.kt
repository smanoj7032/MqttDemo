package com.manoj.base.presentation.common.permissionutils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

private const val TAG = "runWithPermissions"

/**
 * Injects code to ask for permissions before executing any code that requires permissions
 * defined in the annotation
 */
fun Context?.runWithPermissions(
    vararg permissions: String,
    options: QuickPermissionsOptions = QuickPermissionsOptions(),
    callback: () -> Unit
): Any? {
    return runWithPermissionsHandler(this, permissions, callback, options)
}

/**
 * Injects code to ask for permissions before executing any code that requires permissions
 * defined in the annotation
 */
fun Fragment?.runWithPermissions(
    vararg permissions: String,
    options: QuickPermissionsOptions = QuickPermissionsOptions(),
    callback: () -> Unit
): Any? {
    return runWithPermissionsHandler(this, permissions, callback, options)
}

private fun runWithPermissionsHandler(
    target: Any?,
    permissions: Array<out String>,
    callback: () -> Unit,
    options: QuickPermissionsOptions
): Nothing? {
    Log.d(TAG, "runWithPermissions: start")

    // get the permissions defined in annotation
    Log.d(TAG, "runWithPermissions: permissions to check: $permissions")

    // get the context
    val context = when (target) {
        is Context -> target
        is Fragment -> target.requireContext()
        else -> null
    }

    // check if we have the permissions
    if (PermissionsUtil.hasSelfPermission(context, arrayOf(*permissions))) {
        Log.d(
            TAG, "runWithPermissions: already has required permissions. Proceed with the execution."
        )
        callback()
    } else {
        // we don't have required permissions
        // begin the permission request flow

        Log.d(TAG, "runWithPermissions: doesn't have required permissions")

        val fragmentManager = when (target) {
            is AppCompatActivity -> target.supportFragmentManager
            is Fragment -> target.childFragmentManager
            else -> null
        }

        if (fragmentManager != null) {
            var permissionCheckerFragment =
                fragmentManager.findFragmentByTag(PermissionCheckerFragment::class.java.canonicalName) as PermissionCheckerFragment?

            if (permissionCheckerFragment == null) {
                Log.d(TAG, "runWithPermissions: adding headless fragment for asking permissions")
                val newPermissionCheckerFragment = PermissionCheckerFragment.newInstance()

                fragmentManager.beginTransaction().apply {
                    add(
                        newPermissionCheckerFragment,
                        PermissionCheckerFragment::class.java.canonicalName
                    )
                    commitNow()
                }

                permissionCheckerFragment = newPermissionCheckerFragment
            }

            // Set callback to permission checker fragment
            permissionCheckerFragment.setListener(object :
                PermissionCheckerFragment.QuickPermissionsCallback {
                override fun onPermissionsGranted(quickPermissionsRequest: QuickPermissionsRequest?) {
                    Log.d(TAG, "runWithPermissions: got permissions")
                    try {
                        callback()
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                }

                override fun onPermissionsDenied(quickPermissionsRequest: QuickPermissionsRequest?) {
                    quickPermissionsRequest?.permissionsDeniedMethod?.invoke(quickPermissionsRequest)
                }

                override fun shouldShowRequestPermissionsRationale(quickPermissionsRequest: QuickPermissionsRequest?) {
                    quickPermissionsRequest?.rationaleMethod?.invoke(quickPermissionsRequest)
                }

                override fun onPermissionsPermanentlyDenied(quickPermissionsRequest: QuickPermissionsRequest?) {
                    quickPermissionsRequest?.permanentDeniedMethod?.invoke(quickPermissionsRequest)
                }
            })

            // Create permission request instance
            val permissionRequest =
                QuickPermissionsRequest(permissionCheckerFragment, arrayOf(*permissions))
            permissionRequest.handleRationale = options.handleRationale
            permissionRequest.handlePermanentlyDenied = options.handlePermanentlyDenied
            permissionRequest.rationaleMessage =
                options.rationaleMessage.ifBlank { "These permissions are required to perform this feature. Please allow us to use this feature. " }
            permissionRequest.permanentlyDeniedMessage =
                options.permanentlyDeniedMessage.ifBlank { "Some permissions are permanently denied which are required to perform this operation. Please open app settings to grant these permissions." }
            permissionRequest.rationaleMethod = options.rationaleMethod
            permissionRequest.permanentDeniedMethod = options.permanentDeniedMethod
            permissionRequest.permissionsDeniedMethod = options.permissionsDeniedMethod

            // Begin the flow by requesting permissions
            permissionCheckerFragment.setRequestPermissionsRequest(permissionRequest)

            // Start requesting permissions for the first time
            permissionCheckerFragment.requestPermissionsFromUser()
        } else {
            // Context is null or unsupported
            // Cannot handle the permission checking from any class other than AppCompatActivity/Fragment
            // Throw an exception.
            throw IllegalStateException("Found " + target!!::class.java.canonicalName + ": No support from any classes other than AppCompatActivity/Fragment")
        }
    }
    return null
}
