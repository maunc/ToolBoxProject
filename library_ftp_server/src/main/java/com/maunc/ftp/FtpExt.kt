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

/**
 * 获取当前设备局域网IP（供客户端访问）
 */
fun obtainLocalIpAddress(): String {
    val interfaces = java.net.NetworkInterface.getNetworkInterfaces()
    while (interfaces.hasMoreElements()) {
        val `interface` = interfaces.nextElement()
        val addresses = `interface`.inetAddresses
        while (addresses.hasMoreElements()) {
            val address = addresses.nextElement()
            // 过滤回环地址、IPv6地址，只返回IPv4
            if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                return address.hostAddress ?: ""
            }
        }
    }
    return "127.0.0.1"
}