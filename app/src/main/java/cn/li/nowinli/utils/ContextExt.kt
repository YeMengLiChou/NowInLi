package cn.li.nowinli.utils

import android.content.Context
import java.lang.ref.WeakReference

/**
 * 返回 Context 的弱引用
 * */
fun Context.weak() = WeakReference(this)


