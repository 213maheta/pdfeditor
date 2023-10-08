package com.twoonethree.pdfeditor.screencompose

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.twoonethree.pdfeditor.utilities.FileManager

@Composable
fun GetPermission(permission: String)
{
    val permissionLauncher = getPermissionLauncher()
    val permissionArray = arrayOf(
        permission
    )
    LaunchedEffect(key1 = Unit){
        permissionLauncher.launch(permissionArray)
    }
}

@Composable
fun isStorageAccessable(): Boolean {
    val read = checkReadPermission()
    val write = checkWritePermission()
    if(!write)
    {
        GetPermission(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
    if(!read)
    {
        GetPermission(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    return read && write
}

@Composable
fun AppDirectorySetup()
{
    if(FileManager.isAppDirectoryExist())
    {
        return
    }
    if(isStorageAccessable())
    {
        FileManager.createAppDirectory()
    }
}

@Composable
fun checkReadPermission(): Boolean {
    val context = LocalContext.current
    return when{
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true
        else -> ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun checkWritePermission(): Boolean {
    val context = LocalContext.current
    return when{
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> true
        else -> ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun checkCameraPermission(): Boolean {
    val context = LocalContext.current
    return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun getPermissionLauncher(): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {

        }
    )
}