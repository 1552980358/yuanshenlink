package com.qyinter.yuanshenlink.util

import java.math.BigInteger
import java.security.MessageDigest

object Md5Util {

    private const val MESSAGE_DIGEST_MD5 = "MD5"
    private const val MD5_MIN_LENGTH = 32
    private const val MD5_FILLING = '0'

    /**
     * 对字符串 MD5 加密
     *
     * @param str 原始值
     * @return MD5 值
     */
    fun getMD5(str: String) = try {
            // 生成一个MD5加密计算摘要
            MessageDigest.getInstance(MESSAGE_DIGEST_MD5).run {
                // 计算md5函数
                update(str.toByteArray())

                // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
                // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
                BigInteger(1, digest()).toString(16)
                    // 大于等于32直接返回, 不足32则补足到32
                    .padStart(MD5_MIN_LENGTH, MD5_FILLING)
            }
    } catch (e: Exception) {
        null
    }

}