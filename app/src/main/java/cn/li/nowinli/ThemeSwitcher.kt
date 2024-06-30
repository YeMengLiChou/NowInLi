package cn.li.nowinli

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.animation.addListener
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import kotlin.math.roundToInt
import kotlin.math.sqrt

class ThemeSwitcher(view: View) : LifecycleEventObserver, View.OnTouchListener {

    private var weekActivity = WeakReference(view.context as Activity)

    private val activityName = weekActivity.get()!!.javaClass.name

    private val context: Context
        get() = weekActivity.get() as Context

    private val application: Application
        get() = weekActivity.get()!!.applicationContext as Application

    private val window
        get() = weekActivity.get()!!.window

    private val decorView: FrameLayout
        get() = window.decorView as FrameLayout

    private val mainHandler = Handler(Looper.getMainLooper())

    private var lastX  = 0

    private var lastY  = 0

    init {

        view.setOnClickListener {
            switch()
        }
        view.setOnTouchListener(this)

        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity.javaClass.name == activityName) {
                    weekActivity = WeakReference(activity)
                }
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {

            }
        })

    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when(event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val location = IntArray(2)
                v.getLocationInWindow(location)
                lastX = (event.x + location[0]).roundToInt()
                lastY = (event.y + location[1]).roundToInt()
            }
        }
        return v.onTouchEvent(event)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            // clear
        }
    }

    fun switch() {
        // 截图当前状态的屏幕
        val screenShot = window.decorView.drawToBitmap()

        AppCompatDelegate.setDefaultNightMode(
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
        )

        val switchThemeImageView = ImageView(context).apply {
            setImageBitmap(screenShot)
        }

        mainHandler.post {

            val contentView: FrameLayout = window.decorView.findViewById(android.R.id.content)
            val background = contentView.background

            decorView.addView(switchThemeImageView, 0)

            val h = decorView.measuredHeight
            val w = decorView.measuredWidth
            val radius = sqrt((h * h + w * w).toDouble()).toFloat()
            val animator = ViewAnimationUtils.createCircularReveal(
                contentView, lastX, lastY, 0f, radius
            )
//            contentView.visibility = View.INVISIBLE

            animator.duration = 1500
            animator.addListener(
                onStart = {
//                    contentView.visibility = View.VISIBLE
                }, onEnd = {
                    decorView.removeView(switchThemeImageView)
                    switchThemeImageView.setImageBitmap(null)
                })
            animator.start()
        }
    }

}


fun View.setThemeSwitcher() {
    ThemeSwitcher(this)
}