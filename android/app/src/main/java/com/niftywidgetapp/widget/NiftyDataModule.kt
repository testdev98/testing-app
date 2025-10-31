package com.niftywidgetapp.widget

import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

class NiftyDataModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "NiftyDataModule"
    }

    @ReactMethod
    fun getNiftyData(promise: Promise) {
        Thread {
            try {
                val url = URL("https://www.nseindia.com/api/allIndices")
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                connection.setRequestProperty("Accept", "*/*")
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9")

                val `in` = BufferedReader(InputStreamReader(connection.inputStream))
                var inputLine: String?
                val content = StringBuilder()
                while (`in`.readLine().also { inputLine = it } != null) {
                    content.append(inputLine)
                }
                `in`.close()
                connection.disconnect()

                promise.resolve(content.toString())
            } catch (e: Exception) {
                promise.reject("Error", e.message)
            }
        }.start()
    }
}
