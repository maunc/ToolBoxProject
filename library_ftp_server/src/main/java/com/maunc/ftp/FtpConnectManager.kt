package com.maunc.ftp

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

val ftpConnectManager: FtpConnectManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    FtpConnectManager.ftpConnectManager
}

/**
 * FTP 客户端管理器：用于连接“其他 FTP 服务器”
 */
class FtpConnectManager private constructor() {

    companion object {
        const val TAG = "FtpConnectManager"

        val ftpConnectManager: FtpConnectManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FtpConnectManager()
        }
    }

    private var client: FTPClient? = null

    fun isConnected(): Boolean = client?.isConnected == true

    fun disconnect() {
        val c = client
        client = null
        try {
            if (c?.isConnected == true) {
                try {
                    c.logout()
                } catch (_: Exception) {
                }
                try {
                    c.disconnect()
                } catch (_: Exception) {
                }
            }
        } catch (_: Exception) {
        }
    }

    fun connect(config: ConnectConfig): Result<String> {
        if (!config.port.isValidPort()) return Result.Failure("端口号不合理: ${config.port}")
        disconnect()
        val ftpClient = FTPClient()
        ftpClient.controlEncoding = config.controlEncoding
        ftpClient.connectTimeout = config.connectTimeoutMs
        ftpClient.defaultTimeout = config.connectTimeoutMs
        ftpClient.setDataTimeout(config.dataTimeoutMs)
        return try {
            ftpClient.connect(config.host, config.port)
            // 只有在 connect() 成功后，socket 才是非空，才能设置 soTimeout
            ftpClient.soTimeout = config.soTimeoutMs
            if (!FTPReplyHelper.isPositiveCompletion(ftpClient.replyCode)) {
                val msg =
                    "连接失败: ${ftpClient.replyString?.trim() ?: "replyCode=${ftpClient.replyCode}"}"
                try {
                    ftpClient.disconnect()
                } catch (_: Exception) {
                }
                Result.Failure(msg)
            } else if (!ftpClient.login(config.username, config.password)) {
                val msg =
                    "登录失败: ${ftpClient.replyString?.trim() ?: "replyCode=${ftpClient.replyCode}"}"
                try {
                    ftpClient.disconnect()
                } catch (_: Exception) {
                }
                Result.Failure(msg)
            } else {
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                if (config.passiveMode) ftpClient.enterLocalPassiveMode() else ftpClient.enterLocalActiveMode()
                client = ftpClient
                Result.Success("登录成功")
            }
        } catch (t: Throwable) {
            try {
                ftpClient.disconnect()
            } catch (_: Exception) {
            }
            Result.Failure("连接异常: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

   fun list(path: String = "."): Result<List<FTPFile>> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            Result.Success(c.listFiles(path)?.toList() ?: emptyList())
        } catch (t: Throwable) {
            Result.Failure("列目录失败: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun mkdir(path: String): Result<Boolean> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            Result.Success(c.makeDirectory(path))
        } catch (t: Throwable) {
            Result.Failure("创建目录失败: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun delete(remotePath: String): Result<Boolean> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            Result.Success(c.deleteFile(remotePath))
        } catch (t: Throwable) {
            Result.Failure("删除失败: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun download(remotePath: String, localFile: File): Result<Unit> {
        val c = client ?: return Result.Failure("未连接")
        return try {
            localFile.parentFile?.mkdirs()
            FileOutputStream(localFile).use { out ->
                if (!c.retrieveFile(remotePath, out)) {
                    Result.Failure("下载失败: ${c.replyString?.trim() ?: "replyCode=${c.replyCode}"}")
                } else {
                    Result.Success(Unit)
                }
            }
        } catch (t: Throwable) {
            Result.Failure("下载异常: ${t.message ?: t::class.java.simpleName}", t)
        }
    }

    fun upload(localFile: File, remotePath: String): Result<Unit> {
        val c = client ?: return Result.Failure("未连接")
        if (!localFile.exists()) return Result.Failure("本地文件不存在: ${localFile.absolutePath}")

        return try {
            FileInputStream(localFile).use { input ->
                if (!c.storeFile(remotePath, input)) {
                    Result.Failure("上传失败: ${c.replyString?.trim() ?: "replyCode=${c.replyCode}"}")
                } else {
                    Result.Success(Unit)
                }
            }
        } catch (t: Throwable) {
            Result.Failure("上传异常: ${t.message ?: t::class.java.simpleName}", t)
        }
    }
}

data class ConnectConfig(
    var host: String,
    var port: Int,
    var username: String,
    var password: String,
    var connectTimeoutMs: Int = 15_000,
    var dataTimeoutMs: Int = 30_000,
    var soTimeoutMs: Int = 30_000,
    var passiveMode: Boolean = true,
    var controlEncoding: String = "UTF-8",
)

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure<T>(val reason: String, val throwable: Throwable? = null) : Result<T>()
}

/**
 * 避免直接依赖 org.apache.commons.net.ftp.FTPReply（保持 API 更稳定）。
 */
private object FTPReplyHelper {
    fun isPositiveCompletion(reply: Int): Boolean = reply in 200..299
}