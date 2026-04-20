package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.data.model.toUser
import com.doannd3.treetask.core.datastore.UserPrefsManager
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.network.service.UserService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val userPrefsManager: UserPrefsManager
) : UserRepository {

    override suspend fun getProfile(): ApiResult<User> {
        val result = userService.getProfile()
        return when (result) {
            is ApiResult.Success -> {
                val user = result.data.toUser()
                userPrefsManager.saveUserProfile(user)
                ApiResult.Success(user)
            }

            is ApiResult.Error -> {
                result
            }
        }
    }

    override fun getCachedProfile(): Flow<User?> {
        return userPrefsManager.getUserProfile()
    }

}