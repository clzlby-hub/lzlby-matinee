package com.tcltv.sahne.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object BingWallpaperClient {

    private const val TAG = "BingWallpaper"
    private const val API_URL = "https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN"
    private const val BING_HOST = "https://www.bing.com"
    private const val PREFS = "bing_wallpaper_meta"
    private const val KEY_DATE = "startdate"
    private const val KEY_TITLE = "title"
    private const val KEY_COPYRIGHT = "copyright"
    private const val FILE_NAME = "bing_wallpaper.jpg"

    fun getFile(context: Context): File = File(context.filesDir, FILE_NAME)

    fun getTitle(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_TITLE, null)

    fun getCopyright(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_COPYRIGHT, null)

    /**
     * Checks and updates today's Bing 4K wallpaper.
     * Returns true if a new wallpaper was successfully downloaded, or if today's wallpaper is already present.
     */
    suspend fun sync(context: Context, force: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        val file = getFile(context)
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val savedDate = prefs.getString(KEY_DATE, "")

        try {
            val connection = (URL(API_URL).openConnection() as HttpURLConnection).apply {
                connectTimeout = 8000
                readTimeout = 10000
                setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            }
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Bing API returned code ${connection.responseCode}")
                return@withContext file.exists()
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val images = json.optJSONArray("images") ?: return@withContext file.exists()
            if (images.length() == 0) return@withContext file.exists()

            val todayItem = images.getJSONObject(0)
            val startDate = todayItem.optString("startdate")
            val title = todayItem.optString("title")
            val copyright = todayItem.optString("copyright")
            val urlBase = todayItem.optString("urlbase")
            val urlFallback = todayItem.optString("url")

            // If already downloaded and file exists, avoid re-downloading unless forced
            if (!force && file.exists() && savedDate == startDate) {
                return@withContext true
            }

            val targetUrl = when {
                urlBase.isNotEmpty() -> "$BING_HOST${urlBase}_UHD.jpg"
                urlFallback.startsWith("http") -> urlFallback
                else -> "$BING_HOST$urlFallback"
            }

            val downloadSuccess = downloadImage(targetUrl, file) ||
                (urlFallback.isNotEmpty() && downloadImage("$BING_HOST$urlFallback", file))

            if (downloadSuccess) {
                prefs.edit()
                    .putString(KEY_DATE, startDate)
                    .putString(KEY_TITLE, title)
                    .putString(KEY_COPYRIGHT, copyright)
                    .apply()
                Log.i(TAG, "Successfully downloaded Bing daily wallpaper: $title")
                return@withContext true
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync Bing wallpaper: ${e.message}")
        }
        return@withContext file.exists()
    }

    private fun downloadImage(urlStr: String, destFile: File): Boolean {
        val tempFile = File(destFile.parentFile, "${destFile.name}.tmp")
        try {
            val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000
                readTimeout = 15000
                setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            }
            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                return false
            }
            conn.inputStream.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (tempFile.length() > 1024) {
                if (destFile.exists()) destFile.delete()
                return tempFile.renameTo(destFile)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error downloading image from $urlStr: ${e.message}")
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
        return false
    }
}
