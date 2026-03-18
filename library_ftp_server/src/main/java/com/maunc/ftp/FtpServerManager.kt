package com.maunc.ftp

import android.os.Environment
import android.util.Log
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.ftplet.FtpException
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission
import org.apache.ftpserver.usermanager.impl.TransferRatePermission
import org.apache.ftpserver.usermanager.impl.WritePermission
import java.io.File

val ftpServerManager: FtpServerManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    FtpServerManager.ftpServerManager
}
/**
 * FTP  服务端管理器：用于创建“FTP 服务器”
 */
class FtpServerManager private constructor() {
    // FTP服务器实例
    private var ftpServer: FtpServer? = null

    // 默认配置
    companion object {
        const val TAG = "FtpServerManager"
        const val DEFAULT_PORT = 21210  // 冷门端口
        const val DEFAULT_USER = "android" // 访问账号
        const val DEFAULT_PWD = "123456"  // 访问密码

        const val FTP_SHARE_PORT_NOT = "端口号不合理"
        const val FTP_SHARE_DIR_NOT_EXISTS = "共享目录不存在"
        const val FTP_SERVER_STATUS_INIT = "服务创建成功"
        const val FTP_SERVER_STATUS_NONE_INIT = "服务未初始化"
        const val FTP_SERVER_STATUS_STOP = "服务已停止"
        const val FTP_SERVER_STATUS_PAUSE = "服务已暂停"
        const val FTP_SERVER_STATUS_RUNNING = "服务正在运行"

        // 共享目录（安卓公共下载目录，可自定义）
        val DEFAULT_SHARE_DIR: String
            get() = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath

        val ftpServerManager: FtpServerManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FtpServerManager()
        }
    }

    /**
     * 启动FTP服务器
     * @param port 端口号（默认2121）
     * @param username 访问账号
     * @param password 访问密码
     * @param shareDir 共享目录路径
     * @return 启动结果（true=成功，false=失败）
     */
    fun startFtpServer(
        port: Int = DEFAULT_PORT,
        username: String = DEFAULT_USER,
        password: String = DEFAULT_PWD,
        shareDir: String = DEFAULT_SHARE_DIR,
    ): Pair<Boolean, String> {
        if (!port.isValidPort()) return Pair(false, FTP_SHARE_PORT_NOT)
        if (!File(shareDir).exists()) return Pair(false, FTP_SHARE_DIR_NOT_EXISTS)
        if (isServerRunning()) return Pair(false, FTP_SERVER_STATUS_RUNNING)
        stopFtpServer()
        try {
            val serverFactory = FtpServerFactory()
            val listenerFactory = ListenerFactory()
            listenerFactory.port = port // 设置端口
            listenerFactory.serverAddress = "0.0.0.0" // 显式监听所有 IPv4 地址
            serverFactory.addListener("default", listenerFactory.createListener())

            // 3. 配置用户（账号密码、权限、共享目录）
            val userManagerFactory = PropertiesUserManagerFactory()
            val userManager = userManagerFactory.createUserManager()
            val user = BaseUser()
            user.name = username
            user.password = password
            user.homeDirectory = shareDir // 设置共享目录
            // 配置用户权限（读、写、列出文件等）
            // 修复：正确配置权限（使用 Authority 实现类，而非字符串）
            val authorities = mutableListOf<Authority>()
            // 读权限（列出文件、下载等）不限速：0 表示无限制
            authorities.add(TransferRatePermission(0, 0))
            // 写权限（上传、删除、修改等）
            authorities.add(WritePermission())
            // 可选：并发登录权限（允许同时登录的最大数，第一个参数=最大登录数，第二个=每IP最大登录数）
            authorities.add(ConcurrentLoginPermission(10, 2))
            user.authorities = authorities
            userManager.save(user)
            serverFactory.userManager = userManager
            Log.e(TAG, "ftp://${username}:${password}@${obtainLocalIpAddress()}:$port")
            ftpServer = serverFactory.createServer()
            ftpServer?.start()
            return Pair(
                true, "ftp://${obtainLocalIpAddress()}:$port"
            )
        } catch (e: FtpException) {
            e.printStackTrace()
            return Pair(false, "${e.message}")
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, "${e.message}")
        }
    }

    /**
     * 停止FTP服务器
     */
    fun stopFtpServer() {
        if (isServerRunning()) {
            try {
                ftpServer?.stop()
            } catch (_: Exception) {
            } finally {
                ftpServer = null
            }
        }
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

    /**
     * 检查服务器是否正在运行
     */
    fun isServerRunning() = ftpServer != null
            && ftpServer?.isStopped != true
            && ftpServer?.isSuspended != true

    fun obtainServerStatusDesc(): String {
        return when {
            ftpServer == null -> FTP_SERVER_STATUS_NONE_INIT
            ftpServer?.isStopped == true -> FTP_SERVER_STATUS_STOP
            ftpServer?.isSuspended == true -> FTP_SERVER_STATUS_PAUSE
            else -> FTP_SERVER_STATUS_RUNNING
        }
    }
}