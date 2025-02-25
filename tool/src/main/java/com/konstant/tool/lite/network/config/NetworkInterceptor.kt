package com.konstant.tool.lite.network.config

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 时间：2019/5/5 16:34
 * 创建：吕卡
 * 描述：网络拦截器
 */

class NetworkInterceptor(val context: Context) : Interceptor {

    private val map = HashMap<String, Int>()
    private val URL_EXPRESS_GUOGUO = "http://coldsong.cn/letter/api/v1/kuaidi.php"

    init {
        map[URL_EXPRESS_GUOGUO] = 60 * 30
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        Log.d("网络是否可用:", "" + isNetworkAvailable())

        /**
         * 直接发起网络请求，如果请求成功，则进行数据缓存
         * 缓存时间策略：
         *      1、缓存时间为0，表示用户没有针对这次请求进行缓存，那么手动设置缓存为2小时(60*60*2)
         *      2、缓存时间不为0，表示用户已经手动设置了缓存时间，那么以用户手动设置的为准
         */
        Log.d("请求链接", chain.request().url.toString())
        val cacheTime = getCacheTime(chain.request().url.host)
        val time = if (cacheTime == 0) 60 * 60 * 12 else cacheTime
        val response = chain.proceed(chain.request())
        if (response.isSuccessful) {
            return response.newBuilder()
                    .header("Cache-Control", "public, max-age=$time")
                    .build()
        }
        /**
         * 网络不可用或者请求失败时，会走到这里，如果本地有缓存，则根据时间返回缓存，否则，返回失败
         */
        return response.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=$time")
                .build()
    }

    // 判断网络是否可用
    private fun isNetworkAvailable(): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected && networkInfo.state == NetworkInfo.State.CONNECTED) {
            return true
        }
        return false
    }

    // 获取当前链接的缓存时间，用于对指定链接设定缓存时间
    private fun getCacheTime(hosts: String): Int {
        for (entry in map) {
            if (entry.key.contains(hosts))
                return entry.value
            if (hosts.contains(entry.key))
                return entry.value
        }
        return 0
    }
}

