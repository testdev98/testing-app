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
                val data = NiftyDataFetcher.getNiftyData()
                promise.resolve(data)
            } catch (e: Exception) {
                promise.reject("Error", e.message)
            }
        }.start()
    }
}
