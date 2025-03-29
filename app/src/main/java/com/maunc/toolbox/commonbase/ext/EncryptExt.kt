package com.maunc.toolbox.commonbase.ext

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.md5(): String {
    try {
        val hash = MessageDigest.getInstance("MD5").digest(
            toByteArray(StandardCharsets.UTF_8)
        )
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if ((b.toInt() and 0xFF) < 0x10) hex.append("0")
            hex.append(Integer.toHexString(b.toInt() and 0xFF))
        }
        return hex.toString()
    } catch (e: NoSuchAlgorithmException) {
        return ""
    }
}

fun String.sha1(): String {
    try {
        val digest = MessageDigest.getInstance("SHA-1")
        val messageDigest = digest.digest(toByteArray())
        val hex = java.lang.StringBuilder()
        for (b in messageDigest) {
            val shaHex = Integer.toHexString(b.toInt() and 0xFF)
            if (shaHex.length < 2) hex.append(0)
            hex.append(shaHex)
        }
        return hex.toString()
    } catch (e: NoSuchAlgorithmException) {
        return ""
    }
}

fun String.sha256(): String {
    try {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val digest = messageDigest.digest(toByteArray())
        val hex = java.lang.StringBuilder()
        for (b in digest) {
            hex.append(Integer.toHexString((b.toInt() and 0xFF) or 0x100), 1, 3)
        }
        return hex.toString()
    } catch (e: NoSuchAlgorithmException) {
        return ""
    }
}

fun String.sha512(): String {
    try {
        val messageDigest = MessageDigest.getInstance("SHA-512")
        val bytes = messageDigest.digest(toByteArray())
        val hex = java.lang.StringBuilder()
        for (aByte in bytes) {
            val shaHex = Integer.toHexString(0xff and aByte.toInt())
            if (shaHex.length == 1) hex.append('0')
            hex.append(shaHex)
        }
        return hex.toString()
    } catch (e: NoSuchAlgorithmException) {
        return ""
    }
}