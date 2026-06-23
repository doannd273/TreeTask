package com.doannd3.treetask.core.data.respository

import android.content.Context
import android.net.Uri
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.data.mapper.toUserOrNull
import com.doannd3.treetask.core.datastore.user.UserStorage
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.network.model.request.ChangePasswordRequest
import com.doannd3.treetask.core.network.model.request.UpdateProfileRequest
import com.doannd3.treetask.core.network.service.UserService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

private fun Uri.toAvatarPart(context: Context): MultipartBody.Part {
    val mimeType = context.contentResolver.getType(this) ?: "image/jpeg"
    val extension = mimeType.substringAfter("image/", "jpg")
    val bytes =
        context.contentResolver.openInputStream(this)?.use { it.readBytes() }
            ?: error("Cannot read avatar Uri")
    val requestBody = bytes.toRequestBody(mimeType.toMediaType())
    return MultipartBody.Part.createFormData(
        name = "avatar",
        filename = "avatar.$extension",
        body = requestBody,
    )
}

class UserRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val userService: UserService,
        private val userStorage: UserStorage,
    ) : UserRepository {
        override suspend fun uploadFile(uri: Uri): ApiResult<String> {
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType !in SUPPORTED_AVATAR_MIME_TYPES) {
                return ApiResult.Error(
                    appErrorCode = AppErrorCode.UNSUPPORTED_MEDIA_TYPE,
                    exception = IllegalArgumentException("Unsupported MIME type: $mimeType"),
                )
            }
            val avatarPart = uri.toAvatarPart(context)
            val result = userService.uploadFile(avatar = avatarPart)
            return result.mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val avatarUrl =
                    data.avatar?.takeIf { it.isNotBlank() }
                        ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(
                    data = avatarUrl,
                    message = success.message,
                )
            }
        }

        override suspend fun updateProfile(
            fullName: String,
            phone: String,
            avatar: String,
        ): ApiResult<User> {
            val request =
                UpdateProfileRequest(
                    fullName = fullName,
                    phone = phone,
                    avatar = avatar,
                )

            val result = userService.updateProfile(request = request)
            return result.mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val user = data.toUserOrNull() ?: return@mapSuccessResult missingResponseDataError()

                userStorage.saveUserProfile(user = user)
                ApiResult.Success(message = success.message, data = user)
            }
        }

        override suspend fun getProfile(): ApiResult<User> {
            val result = userService.getProfile()
            return result.mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val user = data.toUserOrNull() ?: return@mapSuccessResult missingResponseDataError()

                userStorage.saveUserProfile(user = user)
                ApiResult.Success(data = user)
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
            return result.mapSuccessResult { success ->
                ApiResult.Success(message = success.message)
            }
        }

        override fun getCachedProfile(): Flow<User?> = userStorage.getUserProfile()

        companion object {
            private val SUPPORTED_AVATAR_MIME_TYPES =
                setOf(
                    "image/jpeg",
                    "image/jpg",
                    "image/png",
                    "image/webp",
                )
        }
    }
