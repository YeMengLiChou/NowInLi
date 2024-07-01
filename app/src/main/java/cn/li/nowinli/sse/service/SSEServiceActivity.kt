package cn.li.nowinli.sse.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import cn.li.nowinli.databinding.ActivitySseServiceBinding
import cn.li.nowinli.network.SSEApi
import cn.li.nowinli.sse.common.SSEViewModel
import cn.li.nowinli.utils.checkPermissionGranted
import cn.li.nowinli.utils.checkVersion
import cn.li.nowinli.utils.requestPermissionsChecked
import cn.li.nowinli.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 * 结合 SSE 和 Service 实现后台消息推送
 *
 * Created by YeMengLiChou on 2024/6/29
 * */
@AndroidEntryPoint
class SSEServiceActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySseServiceBinding.inflate(layoutInflater)
    }

    private val serviceIntent by lazy(LazyThreadSafetyMode.NONE) {
        Intent(this@SSEServiceActivity, SSEService::class.java)
    }


    private var message: String = ""

    @Inject
    lateinit var sseApi: SSEApi

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

    @SuppressLint("InlinedApi")
    private fun checkPermission() {
        checkVersion(Build.VERSION_CODES.TIRAMISU) {
            checkPermissionGranted(this, Manifest.permission.POST_NOTIFICATIONS)
                .onDenied {
                    requestPermissionsChecked(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        100
                    )
                }
        }
    }

    private fun initView() {

        binding.buttonStartService.setOnClickListener {
            startService(serviceIntent)
        }

        binding.buttonShutdownService.setOnClickListener {
            stopService(serviceIntent)
        }

        binding.editTextSend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                message = s.toString()
            }
        })

        binding.buttonSend.setOnClickListener {
            if (message.isBlank()) {
                showToast("请输入消息")
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    kotlin.runCatching {
                        sseApi.sendNotification(message)
                    }.onSuccess {
                        withContext(Dispatchers.Main) {
                            showToast("发送成功")
                        }
                    }.onFailure {
                        Log.e("SSEServiceActivity", "initView: ", it)
                        withContext(Dispatchers.Main) {
                            showToast("发送失败")
                        }
                    }
                }
            }
        }
    }

}