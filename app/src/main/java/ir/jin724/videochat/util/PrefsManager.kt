package ir.jin724.videochat.util

import android.content.Context
import android.content.SharedPreferences
import ir.jin724.videochat.data.userRepository.User

class PrefsManager(context: Context) {

    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor
    private val prefsName = "test_prefs"

    init {
        sharedPreferences = context.getSharedPreferences(prefsName, 0)
        editor = sharedPreferences.edit()
    }


    fun getString(key: String): String {
        return sharedPreferences.getString(key, "")!!
    }

    fun putString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun getInt(key: String): Int {
        return sharedPreferences.getInt(key, -1)
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun getLong(key: String): Long {
        return sharedPreferences.getLong(key, -1L)
    }

    fun putLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun putUser(user: User) {
        putInt("userId", user.userId)
        putString("firstName", user.firstName)
        putString("lastName", user.lastName)
        putString("clientId", user.clientId)
        putString("avatar", user.avatar)
        putString("state", user.state)
    }

    fun getUser(): User {
        return User(
            getInt("userId"),
            getString("clientId"),
            getString("firstName"),
            getString("lastName"),
            getString("avatar"),
            getString("state")
        )
    }

}