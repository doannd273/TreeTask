package com.doannd3.treetask.core.domain.usecase.chat.realtime

import com.doannd3.treetask.core.domain.repository.ChatRealtimeRepository
import javax.inject.Inject

class ObserveChatRealtimeEventsUseCase
    @Inject
    constructor(
        private val chatRealtimeRepository: ChatRealtimeRepository,
    ) {
        operator fun invoke() = chatRealtimeRepository.observeEvents()
    }
