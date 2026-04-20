package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.request.UserRequest
import com.doannd3.treetask.core.network.model.response.AvatarUploadResponse
import com.doannd3.treetask.core.network.model.response.UserResponse
import com.doannd3.treetask.core.network.model.response.UsersListResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface UserService {

    @GET("/api/user/getProfile")
    suspend fun getProfile(): ApiResult<UserResponse>

    @PUT("/api/user/updateProfile")
    suspend fun updateProfile(
        @Body request: UserRequest
    ): ApiResult<UserResponse>

    @Multipart
    @POST("/api/user/uploadAvatar")
    suspend fun uploadFile(
        @Part avatar: MultipartBody.Part
    ): ApiResult<AvatarUploadResponse>

    @GET("/api/user/searchUsers")
    suspend fun searchUsers(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResult<UsersListResponse>
}