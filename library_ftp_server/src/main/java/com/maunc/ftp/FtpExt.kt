package com.maunc.ftp

/**
 * 校验端口是否合理，且避免常见系统端口
 * @param allowSystemPorts 是否允许 1~1023 的系统端口
 */
fun Int.isValidPort(allowSystemPorts: Boolean = false): Boolean {
    if (this !in 1..65535) return false
    if (!allowSystemPorts && this in 1..1023) return false
    return true
}