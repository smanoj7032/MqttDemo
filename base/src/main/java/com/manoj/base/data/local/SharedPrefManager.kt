package com.manoj.base.data.local

import android.content.SharedPreferences

import com.google.gson.Gson
import com.manoj.base.data.bean.UpdateProfileResponse
import com.manoj.base.presentation.common.getValue
import com.manoj.base.presentation.common.saveValue
import javax.inject.Inject

open class SharedPrefManager @Inject constructor(val sharedPreferences: SharedPreferences) {

    private val gson = Gson()


    companion object {
        const val USER = "user"
        const val ACCESS_TOKEN = "access_token"
        const val TOKEN = "token"
        const val USER_EMAIL = "user_email"
        const val UPDATED_PROFILE = "updated_profile"
        const val EMAIL_AT_FORGOT = "email_at_forgot_password"
        const val LOGIN_USING = "login_using"
        const val PROFILE_PIC = "profile_pic"
        const val USER_NAME = "user_name"
        const val File_PATH = "file_path"
    }

    fun saveUserName(name: String?) {
        sharedPreferences.saveValue(USER_NAME, name)
    }

    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME, "").toString()
    }

    fun saveAccessToken(token: String?) {
        sharedPreferences.saveValue(ACCESS_TOKEN, token)
    }

    fun getAccessToken(): String {
        return sharedPreferences.getValue<String>(ACCESS_TOKEN, null).toString()
    }

    fun saveProfilePic(profilePicUrl: String) {
        sharedPreferences.saveValue(PROFILE_PIC, profilePicUrl)
    }

    fun getProfilePicUrl(): String {
        return sharedPreferences.getString(PROFILE_PIC, null).toString()
    }

    fun saveLoginUserDetails(bean: UpdateProfileResponse?) {
        sharedPreferences.saveValue(TOKEN, gson.toJson(bean))
    }

    fun saveLoginPlatform(loginUsing: String) {
        sharedPreferences.saveValue(LOGIN_USING, loginUsing)
    }

    fun getLoginPlatform(): String {
        return sharedPreferences.getValue<String>(LOGIN_USING, null).toString()
    }

    fun getLoginUserDetails(): UpdateProfileResponse? {
        return try {
            val s: String? = sharedPreferences.getValue(TOKEN, null)
            gson.fromJson(s, UpdateProfileResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveUpdatedProfile(updatedProfileData: UpdateProfileResponse) {
        sharedPreferences.saveValue(UPDATED_PROFILE, gson.toJson(updatedProfileData))
    }

    fun getUpdatedProfile(): UpdateProfileResponse? {
        return try {
            val s: String? = sharedPreferences.getValue(UPDATED_PROFILE, null);
            gson.fromJson(s, UpdateProfileResponse::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveUser(bean: UpdateProfileResponse) {
        sharedPreferences.saveValue(USER, gson.toJson(bean))
    }

    fun getCurrentUser(): UpdateProfileResponse? {
        return try {
            val s: String? = sharedPreferences.getValue(USER, null)
            gson.fromJson(s, UpdateProfileResponse::class.java)
        } catch (e: Exception) {
            null
        }

    }

    fun saveUserEmailAtLogin(email: String) {
        sharedPreferences.saveValue(USER_EMAIL, email)
    }

    fun getCurrentUserEmail(): String {

        return sharedPreferences.getValue<String?>(USER_EMAIL, null).toString()
    }

    fun saveEmailAtForgotPassword(email: String) {
        sharedPreferences.saveValue(EMAIL_AT_FORGOT, email)

    }

    fun getEmailAtForgotPassword(): String {
        return sharedPreferences.getValue<String>(EMAIL_AT_FORGOT, null).toString()
    }

    fun clearUser() {
        sharedPreferences.edit().clear().apply()
    }

}


