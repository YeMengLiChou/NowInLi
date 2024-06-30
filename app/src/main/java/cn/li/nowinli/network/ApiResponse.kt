package cn.li.nowinli.network

data class ApiResponse<T>(
    val code: Int,
    val msg: String,
    val data: T?
)