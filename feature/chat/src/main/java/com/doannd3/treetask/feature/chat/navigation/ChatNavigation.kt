package com.doannd3.treetask.feature.chat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object ChatGraphRoute

@Serializable
data object ConversationRoute

fun NavController.navigateToChatGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ChatGraphRoute, navOptions = navOptions)
}

fun NavController.navigateToConversation(navOptions: NavOptions? = null) {
    this.navigate(route = ConversationRoute, navOptions = navOptions)
}

fun NavGraphBuilder.chatGraph() {
    navigation<ChatGraphRoute>(
        startDestination = ConversationRoute
    ) {

    }
}