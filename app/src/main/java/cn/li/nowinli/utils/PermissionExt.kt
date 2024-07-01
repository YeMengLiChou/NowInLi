package cn.li.nowinli.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import cn.li.nowinli.MainActivity


@JvmInline
value class PermissionResult(private val granted: Boolean) {

    fun onGranted(action: () -> Unit): PermissionResult {
        if (granted) {
            action()
        }
        return this
    }

    fun onDenied(action: () -> Unit): PermissionResult {
        if (!granted) {
            action()
        }
        return this
    }
}

/**
 * 检查权限是否已经被授予
 * */
fun checkPermissionGranted(
    context: Context,
    permission: String,
): PermissionResult {
    return PermissionResult(
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    )
}


@SuppressLint("NewApi")
fun requestPermissionsChecked(
    activity: Activity,
    permissions: Array<out String>,
    requestCode: Int,
) {
    checkVersion(Build.VERSION_CODES.M) {
        activity.requestPermissions(permissions, requestCode)
    }
}

/*
1. 访问权限
2. 首次拒绝
3. 弹出弹窗显示必要性，再次获取
4. 再次拒绝
5. 再次使用功能时，显示需要去设置开启功能


* */