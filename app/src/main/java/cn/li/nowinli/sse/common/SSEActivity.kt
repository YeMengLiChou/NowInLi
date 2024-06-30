package cn.li.nowinli.sse.common

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import cn.li.nowinli.databinding.ActivitySseBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

@AndroidEntryPoint
class SSEActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivitySseBinding.inflate(layoutInflater)
    }

    private val viewModel: SSEViewModel by viewModels()

    private lateinit var sseMessageAdapter: SSEMessageAdapter

    private var sendMessage: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.realEventSourceState.collect {
                Log.d("SSEActivity", "sse changed: $it")
                val message = when (it) {
                    is SSEState.Event -> {
                        SSEMessage(
                            "onEvent: ${it.id} ${it.data} ${it.type}",
                            Instant.now().toEpochMilli()
                        )
                    }

                    is SSEState.Nothing -> {
                        SSEMessage(
                            "Nothing:",
                            Instant.now().toEpochMilli()
                        )
                    }

                    is SSEState.Open -> {
                        SSEMessage(
                            "onOpen: ${it.response}",
                            Instant.now().toEpochMilli()
                        )
                    }

                    is SSEState.Closed -> {
                        SSEMessage(
                            "onClosed:",
                            Instant.now().toEpochMilli()
                        )
                    }

                    is SSEState.Failure -> {
                        SSEMessage(
                            "onFailure: ${it.t} ${it.response}",
                            Instant.now().toEpochMilli()
                        )
                    }
                }
                withContext(Dispatchers.Main) {
                    sseMessageAdapter.addMessage(message)
                }
            }
        }

        initView()
    }

    private fun initView() {
        binding.recyclerViewSseMessageList.adapter =
            SSEMessageAdapter(this, mutableListOf()).apply {
                sseMessageAdapter = this
            }

        binding.recyclerViewSseMessageList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.editTextSend.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                sendMessage = s.toString()
            }
        })

        binding.buttonSend.setOnClickListener {
            if (sendMessage.isBlank()) {
                Toast.makeText(this, "please input message", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sendMessage(sendMessage)
                binding.editTextSend.text?.clear()
            }
        }
    }
}
