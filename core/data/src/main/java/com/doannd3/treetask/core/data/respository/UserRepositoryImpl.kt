package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.MissingResponseDataException
import com.doannd3.treetask.core.data.model.toUserOrNull
import com.doannd3.treetask.core.datastore.user.UserStorage
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.network.model.request.ChangePasswordRequest
import com.doannd3.treetask.core.network.service.UserService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepositoryImpl
@Inject
constructor(
    private val userService: UserService,
    private val userStorage: UserStorage,
) : UserRepository {
    override suspend fun getProfile(): ApiResult<User> {
        val result = userService.getProfile()
        return when (result) {
            is ApiResult.Success -> {
                val data = result.data
                if (data == null) {
                    return ApiResult.Error(
                        appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                        exception = MissingResponseDataException(),
                    )
                }

                val user = data.toUserOrNull()
                if (user == null) {
                    return ApiResult.Error(
                        appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                        exception = MissingResponseDataException(),
                    )
                }

                userStorage.saveUserProfile(user)
                ApiResult.Success(data = user)
            }

            is ApiResult.Error -> {
                result
            }
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): ApiResult<String> {
        val body =
            ChangePasswordRequest(
                currentPassword = currentPassword,
                newPassword = newPassword,
            )
        val result = userService.changePassword(body = body)
        return when (result) {
            is ApiResult.Success -> {
                ApiResult.Success(message = result.message)
            }

            is ApiResult.Error -> {
                result
            }
        }
    }

    override fun getCachedProfile(): Flow<User?> = userStorage.getUserProfile()
}
