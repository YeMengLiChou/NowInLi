package cn.li.nowinli.utils

import android.app.Activity
import java.lang.ref.WeakReference


fun Activity.weakActivity() = WeakReference(this)