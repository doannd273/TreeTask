package com.doannd3.treetask.core.datastore.user

import com.doannd3.treetask.core.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserStorage {
    fun getUserProfile(): Flow<User?>
    suspend fun saveUserProfile(user: User)
    suspend fun clearUserProfile()
}
