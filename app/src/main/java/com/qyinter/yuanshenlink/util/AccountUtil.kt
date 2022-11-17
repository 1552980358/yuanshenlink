package com.qyinter.yuanshenlink.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.qyinter.yuanshenlink.util.Md5Util.getMD5
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object AccountUtil {
    
    private const val TIMEOUT_SECOND = 10L
    
    private const val REQUEST_LOGIN_TOKENS_COOKIE = "Cookie"
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECOND, TimeUnit.SECONDS)
        .build()
    
    private fun request(url: String,
                        cookie: String,
                        postContent: String? = null) =
        request(url, mapOf(REQUEST_LOGIN_TOKENS_COOKIE to cookie), postContent)
    
    private fun request(url: String,
                        propertyMap: Map<String, String>,
                        postContent: String? = null): String? {
        return okHttpClient.newCall(
            Request.Builder()
                .url(url)
                .headers(propertyMap.toHeaders())
                .apply {
                    postContent?.let { post(it.toRequestBody("application/json;charset=utf-8".toMediaType())) }
                }
                .build()
        ).execute().body?.string()
    }
    
    private const val JSON_DATA = "data"
    
    private const val JSON_DATA_ACCOUNT_INFO = "account_info"
    private const val JSON_DATA_ACCOUNT_INFO_ACCOUNT_ID = "account_id"
    private const val JSON_DATA_ACCOUNT_INFO_WEB_LOGIN_TOKEN = "weblogin_token"
    private val urlLoginByCookie
        get() = "https://webapi.account.mihoyo.com/Api/login_by_cookie?t=${System.currentTimeMillis()}"
    /**
     * https://github.com/qyinter/yuanshenlink/blob/master/app/src/main/java/com/qyinter/yuanshenlink/http/OkHttpUtil.kt#L29
     **/
    @JvmStatic
    fun requestLoginTokens(cookie: String): Account? {
        // uid => data.account_info.account_id
        // token_types => loginCookieData.data.account_info.weblogin_token
        val accountInfo = try {
            JsonParser.parseString(request(urlLoginByCookie, cookie)).asJsonObject
                .getAsJsonObject(JSON_DATA)                 // data
                .getAsJsonObject(JSON_DATA_ACCOUNT_INFO)    // account_info
        } catch (e: Exception) {
            return null
        }
        return Account(
            accountInfo[JSON_DATA_ACCOUNT_INFO_ACCOUNT_ID].asString,
            accountInfo[JSON_DATA_ACCOUNT_INFO_WEB_LOGIN_TOKEN].asString
        )
    }
    
    private fun getUrlPrefixRequestTid(account: Account) =
        "https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket?" +
            "login_ticket=${account.token}&" +
            "token_types=3&" +
            "uid=${account.uid}"
        
    private const val JSON_DATA_LIST = "list"
    private const val JSON_DATA_LIST_NAME = "name"
    private const val JSON_DATA_LIST_TOKEN = "token"
    /**
     * https://github.com/qyinter/yuanshenlink/blob/master/app/src/main/java/com/qyinter/yuanshenlink/http/OkHttpUtil.kt#L45
     **/
    fun requestTidCookie(account: Account, cookie: String): String? {
        val list = try {
            // list => data.list
            JsonParser.parseString(request(getUrlPrefixRequestTid(account), cookie)).asJsonObject
                .getAsJsonObject(JSON_DATA)         // data
                .getAsJsonArray(JSON_DATA_LIST)     // list
        } catch (e: Exception) {
            return null
        }
        
        return StringBuilder("stuid=${account.uid};").apply {
            list.forEach {
                append(
                    // name => data.list.name
                    // token => data.list.token
                    "${it.asJsonObject[JSON_DATA_LIST_NAME].asString}=${it.asJsonObject[JSON_DATA_LIST_TOKEN].asString};"
                )
            }
            append(cookie)
        }.toString()
    }
    
    private const val JSON_DATA_LIST_GAME_UID = "game_uid"
    private const val JSON_DATA_LIST_GAME_GAME_BIZ = "game_biz"
    private const val JSON_DATA_LIST_GAME_GAME_REGION = "region"
    private const val URL_USER_SERVICE = "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hk4e_cn"
    fun requestUserServiceList(cookie: String): List<UserService>? {
        val userServices = try {
            JsonParser.parseString(request(URL_USER_SERVICE, cookie)).asJsonObject
                .getAsJsonObject(JSON_DATA)     // data
                .getAsJsonArray(JSON_DATA_LIST) // list
        } catch (e: Exception) {
            return null
        }
        
        return arrayListOf<UserService>().apply {
            userServices.forEach {
                add(
                    it.asJsonObject.let { userService ->
                        UserService(
                            // game_uid => data.list.game_uid
                            userService[JSON_DATA_LIST_GAME_UID].asString,
                            // game_biz => data.list.game_biz
                            userService[JSON_DATA_LIST_GAME_GAME_BIZ].asString,
                            // region => data.list.region
                            userService[JSON_DATA_LIST_GAME_GAME_REGION].asString
                        )
                    }
                )
            }
        }
    }
    
    private const val JSON_DATA_AUTHKEY = "authkey"
    private const val AUTH_KEY_POST_DATA_KEY_AUTH_APPID = "auth_appid"
    private const val AUTH_KEY_POST_DATA_VALUE_AUTH_APPID = "webview_gacha"
    private const val AUTH_KEY_POST_DATA_KEY_GAME_BIZ = "game_biz"
    private const val AUTH_KEY_POST_DATA_KEY_GAME_UID = "game_uid"
    private const val AUTH_KEY_POST_DATA_KEY_REGION = "region"
    private const val URL_GET_AUTH_KEY = "https://api-takumi.mihoyo.com/binding/api/genAuthKey"
    private const val AUTH_KEY_ENCODE_CHARSET = "utf-8"
    /**
     * https://github.com/qyinter/yuanshenlink/blob/master/app/src/main/java/com/qyinter/yuanshenlink/http/OkHttpUtil.kt#L82
     **/
    fun requestAuthKey(userService: UserService, cookie: String): String? {
        val jsonPost = JsonObject().apply {
            addProperty(AUTH_KEY_POST_DATA_KEY_AUTH_APPID, AUTH_KEY_POST_DATA_VALUE_AUTH_APPID)
            addProperty(AUTH_KEY_POST_DATA_KEY_GAME_BIZ, userService.gameBiz)
            addProperty(AUTH_KEY_POST_DATA_KEY_GAME_UID, userService.gameUid)
            addProperty(AUTH_KEY_POST_DATA_KEY_REGION, userService.region)
        }.toString()
        val headers = mapOf(
            "Content-Type" to "application/json;charset=utf-8",
            "Host" to "api-takumi.mihoyo.com",
            "Accept" to "application/json, text/plain, */*",
            "x-rpc-app_version" to "2.28.1",
            "x-rpc-client_type" to "5",
            "x-rpc-device_id" to "CBEC8312-AA77-489E-AE8A-8D498DE24E90",
            "DS" to ds,
            "Cookie" to cookie
        )
        // authkey => data.authkey
        val authKeyData = try {
            JsonParser.parseString(request(URL_GET_AUTH_KEY, headers, jsonPost)).asJsonObject
                .getAsJsonObject(JSON_DATA)         // data
                .get(JSON_DATA_AUTHKEY).asString    // authkey
        } catch (e: Exception) {
            return null
        }
        
        return URLEncoder.encode(authKeyData, AUTH_KEY_ENCODE_CHARSET)
    }
    
    private val ds: String
        get() {
            val time = System.currentTimeMillis() / 1000
            val string = randomChars
            return "${time},${string},${getMD5("salt=ulInCDohgEs557j0VsPDYnQaaz6KJcv5&t=$time&r=${string}")}"
        }
    
    private const val RANDOM_CHARS_MAX_REPEAT = 6
    private val randomChars: String
        get() = StringBuilder().apply {
            val chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
            repeat(RANDOM_CHARS_MAX_REPEAT) {
                append(chars[(chars.indices).random()])
            }
        }.toString()

}