package cn.li.nowinli.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.IntRange
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * 检查当前的应用的 SDK 版本，如果符合则执行 [handle]
 * */
@OptIn(ExperimentalContracts::class)
inline fun checkVersion(
    @IntRange(from = 1)
    lowSdk: Int,
    @IntRange(from = 1)
    highSdk: Int = Int.MAX_VALUE,
    @SuppressLint("InlinedApi")
    handle: () -> Unit
) {
    contract {
        callsInPlace(handle, InvocationKind.AT_MOST_ONCE)
    }
    val currentSdk = Build.VERSION.SDK_INT
    if (currentSdk in lowSdk..highSdk) {
        handle.invoke()
    }
}