package cn.li.nowinli.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.li.nowinli.bean.ProjectInfo
import cn.li.nowinli.databinding.LayoutRecyclerviewCardItemBinding
import cn.li.nowinli.utils.formatTimestamp
import cn.li.nowinli.utils.weakActivity

class ProjectItemAdapter(
    context: Activity,
    private val projectList: List<ProjectInfo>,
) : RecyclerView.Adapter<ProjectItemViewHolder>() {

    private val weakContext = context.weakActivity()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectItemViewHolder {
        return ProjectItemViewHolder(
            LayoutRecyclerviewCardItemBinding.inflate(
                weakContext.get()!!.layoutInflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProjectItemViewHolder, position: Int) {
        val item = projectList[position]
        holder.binding.apply {
            textViewTitle.text = item.title
            textViewDescription.text = item.desc
            textViewDate.text = item.time.formatTimestamp("yyyy-MM-dd HH:mm")
            buttonView.setOnClickListener {
                item.action.invoke()
            }
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }
}

class ProjectItemViewHolder(val binding: LayoutRecyclerviewCardItemBinding) :
    RecyclerView.ViewHolder(binding.root)