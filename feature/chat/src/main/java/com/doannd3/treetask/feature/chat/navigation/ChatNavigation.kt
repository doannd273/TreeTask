package com.doannd3.treetask.feature.chat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.doannd3.treetask.feature.chat.ui.conversation.ConversationRoute
import kotlinx.serialization.Serializable

@Serializable
data object ChatGraphDestination

@Serializable
data object ConversationDestination

fun NavController.navigateToChatGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ChatGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToConversation(navOptions: NavOptions? = null) {
    this.navigate(route = ConversationDestination, navOptions = navOptions)
}

fun NavGraphBuilder.chatGraph() {
    navigation<ChatGraphDestination>(
        startDestination = ConversationDestination,
    ) {
        composable<ConversationDestination> {
            ConversationRoute()
        }
    }
}
