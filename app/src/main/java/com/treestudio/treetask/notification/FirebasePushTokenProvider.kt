package com.treestudio.treetask.notification

import com.doannd3.treetask.core.domain.repository.PushTokenProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePushTokenProvider
    @Inject
    constructor() : PushTokenProvider {
        override suspend fun getToken(): String? {
            return FirebaseMessaging.getInstance().token.await()
        }
    }
