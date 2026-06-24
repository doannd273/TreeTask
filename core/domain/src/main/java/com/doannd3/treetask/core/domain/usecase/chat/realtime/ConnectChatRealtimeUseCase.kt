package com.doannd3.treetask.core.domain.usecase.chat.realtime

import com.doannd3.treetask.core.domain.repository.ChatRealtimeRepository
import javax.inject.Inject

class ConnectChatRealtimeUseCase
    @Inject
    constructor(
        private val chatRealtimeRepository: ChatRealtimeRepository,
    ) {
        suspend operator fun invoke() = chatRealtimeRepository.connect()
    }
