package com.nxlauncher.data.net

import com.nxlauncher.data.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object Http {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun get(url: String, headers: Map<String, String> = emptyMap()): String =
        withContext(Dispatchers.IO) {
            val builder = Request.Builder()
                .url(url)
                .header("User-Agent", Constants.USER_AGENT)
            headers.forEach { (key, value) -> builder.header(key, value) }
            client.newCall(builder.build()).execute().use { response ->
                if (!response.isSuccessful) {
                    throw HttpException(response.code, "Request failed with code " + response.code)
                }
                response.body?.string() ?: throw HttpException(response.code, "Empty response body")
            }
        }
}

class HttpException(val code: Int, message: String) : Exception(message)
