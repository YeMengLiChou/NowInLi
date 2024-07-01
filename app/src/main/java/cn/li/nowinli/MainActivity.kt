package cn.li.nowinli

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.li.nowinli.adapter.ProjectItemAdapter
import cn.li.nowinli.bean.ProjectInfo
import cn.li.nowinli.databinding.ActivityMainBinding
import cn.li.nowinli.sse.common.SSEActivity
import cn.li.nowinli.sse.service.SSEServiceActivity

class MainActivity : AppCompatActivity() {
    companion object {
        val TAG = MainActivity::class.simpleName
    }

    private val binding by lazy(mode = LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }


    private val projectInfos = listOf(
        ProjectInfo("SSE服务端推送通知-1", "服务端通过SSE发送消息给客户端", 1719154693110) {
            startActivity(Intent().apply {
                setClass(this@MainActivity, SSEActivity::class.java)
                setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
        }
        ,
        ProjectInfo("SSE服务端推送通知-2", "结合Service发送消息", 1719154693110) {
            startActivity(Intent().apply {
                setClass(this@MainActivity, SSEServiceActivity::class.java)
                setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }


    private fun initView() {
        binding.recyclerviewContent.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = ProjectItemAdapter(this@MainActivity, projectInfos)
        }
    }

}
