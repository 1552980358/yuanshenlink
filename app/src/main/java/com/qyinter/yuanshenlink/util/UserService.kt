package com.qyinter.yuanshenlink.util

data class UserService(val gameUid: String, val gameBiz: String, val region: String) {
    lateinit var authKey: String
    
    val url: String
        get() = "https://hk4e-api.mihoyo.com/event/gacha_info/api/getGachaLog?" +
            "win_mode=fullscreen&" +
            "authkey_ver=1&" +
            "sign_type=2&" +
            "auth_appid=webview_gacha&" +
            "init_type=301&" +
            "gacha_id=b4ac24d133739b7b1d55173f30ccf980e0b73fc1&" +
            "lang=zh-cn&" +
            "device_type=mobile&" +
            "game_version=CNRELiOS3.0.0_R10283122_S10446836_D10316937&" +
            "plat_type=ios&" +
            "game_biz=${gameBiz}&" +
            "size=20&" +
            "authkey=${authKey}&" +
            "region=${region}&" +
            "timestamp=1664481732&" +
            "gacha_type=200&" +
            "page=1&" +
            "end_id=0"

}