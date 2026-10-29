package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("blog_session", Context.MODE_PRIVATE)

    fun loginUser(userId: Int, username: String, token: String? = null) {
        val knownUsers = getKnownUserIds().toMutableSet()
        knownUsers.add(userId.toString())
        
        prefs.edit()
            .putInt("user_id", userId)
            .putString("username", username)
            .putString("auth_token", token)
            .putStringSet("known_user_ids", knownUsers)
            .apply()
    }

    fun getKnownUserIds(): Set<String> {
        return prefs.getStringSet("known_user_ids", emptySet()) ?: emptySet()
    }

    fun logout() {
        // We only clear the current session, NOT the known users list
        prefs.edit()
            .remove("user_id")
            .remove("username")
            .remove("auth_token")
            .apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun getServerUrl(): String {
        return prefs.getString("server_url", "http://10.0.2.2:4000/")!!
    }

    fun setServerUrl(url: String) {
        val cleanUrl = if (url.endsWith("/")) url else "$url/"
        prefs.edit().putString("server_url", cleanUrl).apply()
    }

    fun getLoggedInUser(): User? {
        val id = prefs.getInt("user_id", -1)
        val username = prefs.getString("username", null)
        if (id != -1 && username != null) {
            return User(id = id, username = username)
        }
        return null
    }

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean("dark_theme", enabled).apply()
    }

    fun isDarkTheme(): Boolean {
        return prefs.getBoolean("dark_theme", false)
    }

    fun getAgoraAppId(): String? {
        return prefs.getString("agora_app_id", null)
    }

    fun setAgoraAppId(appId: String?) {
        prefs.edit().putString("agora_app_id", appId).apply()
    }
}
