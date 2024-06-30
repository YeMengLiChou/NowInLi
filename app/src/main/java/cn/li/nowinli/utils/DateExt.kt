package cn.li.nowinli.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 格式化时间戳
 * @param pattern 时间格式
 *  - y 年
 *  - M 月
 *  - d 日
 *  - H 时
 *  - m 分
 *  - s 秒
 * @param zoneId 时区
 * @return 格式化后的时间字符串
 * */
fun Long.formatTimestamp(
    pattern: String,
    zoneId: ZoneId = ZoneId.systemDefault()
): String {
    val time = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return formatter.format(time)
}