package cn.li.nowinli.sse.common

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import cn.li.nowinli.databinding.LayoutSseMessageBinding
import cn.li.nowinli.utils.formatTimestamp
import cn.li.nowinli.utils.weakActivity

class SSEMessageAdapter(
    activity: Activity,
    private val messageList: MutableList<SSEMessage>
): RecyclerView.Adapter<SSEMessageViewHolder>() {

    private val weekContext = activity.weakActivity()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SSEMessageViewHolder {
        return SSEMessageViewHolder(
            LayoutSseMessageBinding.inflate(weekContext.get()!!.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SSEMessageViewHolder, position: Int) {
        messageList[position].let { item ->
            holder.binding.apply {
                textViewSseMessage.text = item.message
                textViewSseDate.text = item.timestamp.formatTimestamp("yyyy-MM-dd HH:mm")
            }
        }
    }

    fun addMessage(message: SSEMessage) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}

class SSEMessageViewHolder(
    val binding: LayoutSseMessageBinding
): ViewHolder(binding.root) {

}