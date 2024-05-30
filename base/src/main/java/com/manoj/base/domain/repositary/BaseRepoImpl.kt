package com.manoj.base.domain.repositary

import com.manoj.base.network.api.BaseApi
import com.manoj.base.data.local.SharedPrefManager
import javax.inject.Inject


class BaseRepoImpl @Inject constructor(
    val apiService: BaseApi,
    private val sharedPrefManager: SharedPrefManager,
) : BaseRepo {

    fun getAuthToken(): String {
        return "Bearer " + sharedPrefManager.getAccessToken()
    }


}
