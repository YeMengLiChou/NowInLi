package cn.li.nowinli.utils

import android.content.Context
import android.widget.Toast


private var mToast: Toast? = null


@JvmName("showToast")
fun showToast(context: Context, msg: String, duration: Int = Toast.LENGTH_SHORT) {
    mToast?.cancel()
    mToast = Toast.makeText(context, msg, duration)
    mToast?.show()
}

@JvmName("contextShowToast")
fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    showToast(this, msg, duration)
}