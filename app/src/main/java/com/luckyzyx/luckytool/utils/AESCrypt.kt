package com.luckyzyx.luckytool.utils

import android.annotation.SuppressLint
import com.joom.paranoid.Obfuscate
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Obfuscate
object AESCrypt {
    private val cryptName = "AES"

    @SuppressLint("GetInstance")
    fun encrypt(data: String, key: String = CommandUtils.aesCryptKey): String {
        //初始化cipher对象
        val cipher = Cipher.getInstance(cryptName)
        // 生成密钥
        val keySpec = SecretKeySpec(key.toByteArray(), cryptName)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        //加密解密
        val encrypt = cipher.doFinal(data.toByteArray())
        val result = Base64.getMimeEncoder().encode(encrypt)

        return String(result)
    }

    @SuppressLint("GetInstance")
    fun decrypt(data: String, key: String = CommandUtils.aesCryptKey): String {
        //初始化cipher对象
        val cipher = Cipher.getInstance(cryptName)
        // 生成密钥
        val keySpec = SecretKeySpec(key.toByteArray(), cryptName)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        //加密解密
        val encrypt = cipher.doFinal(Base64.getMimeDecoder().decode(data.toByteArray()))
        //AES解密不需要用Base64解码
        return String(encrypt)
    }

    fun baseEntrypt(data: String): String {
        val encrypt = Base64.getMimeEncoder().encode(data.toByteArray())
        return String(encrypt)
    }

    fun baseDetrypt(data: String): String {
        val decrypt = Base64.getMimeDecoder().decode(data.toByteArray())
        return String(decrypt)
    }
}