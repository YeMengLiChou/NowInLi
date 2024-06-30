package cn.li.nowinli.bean

data class ProjectInfo(
    val title: String,
    val desc: String,
    val time: Long,
    val action: () -> Unit
)