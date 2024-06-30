package cn.li.nowinli.sse.service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.li.nowinli.databinding.ActivitySseServiceBinding


/**
 * 结合 SSE 和 Service 实现后台消息推送
 *
 * Created by YeMengLiChou on 2024/6/29
 * */
class SSEServiceActivity: AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySseServiceBinding.inflate(layoutInflater)
    }

    private val serviceIntent by lazy(LazyThreadSafetyMode.NONE) {
        Intent(this@SSEServiceActivity, SSEService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initView()
        checkPermission()
    }


    private fun checkPermission() {

        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
    }

    private fun initView() {
        binding.textViewServiceStatus


        binding.buttonStartService.setOnClickListener {
            startService(serviceIntent)
        }

        binding.buttonShutdownService.setOnClickListener {
            stopService(serviceIntent)
        }


    }

}