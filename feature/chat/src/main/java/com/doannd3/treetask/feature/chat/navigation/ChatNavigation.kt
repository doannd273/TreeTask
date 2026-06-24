package com.doannd3.treetask.feature.chat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.doannd3.treetask.feature.chat.ui.conversation.ConversationRoute
import com.doannd3.treetask.feature.chat.ui.detail.ChatDetailRoute
import kotlinx.serialization.Serializable

@Serializable
data object ChatGraphDestination

@Serializable
data object ConversationDestination

@Serializable
data class ChatDetailDestination(
    val conversationId: String,
)

fun NavController.navigateToChatDetail(
    conversationId: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = ChatDetailDestination(conversationId = conversationId),
        navOptions = navOptions,
    )
}

fun NavController.navigateToChatGraph(navOptions: NavOptions? = null) {
    this.navigate(route = ChatGraphDestination, navOptions = navOptions)
}

fun NavController.navigateToConversation(navOptions: NavOptions? = null) {
    this.navigate(route = ConversationDestination, navOptions = navOptions)
}

fun NavGraphBuilder.chatGraph(
    onNavigateToChatDetail: (String) -> Unit,
    onNavigateToBack: () -> Unit,
) {
    navigation<ChatGraphDestination>(
        startDestination = ConversationDestination,
    ) {
        composable<ConversationDestination> {
            ConversationRoute(
                onNavigationToChatDetail = onNavigateToChatDetail,
            )
        }

        composable<ChatDetailDestination> { backStackEntry ->
            val destination = backStackEntry.toRoute<ChatDetailDestination>()

            ChatDetailRoute(
                conversationId = destination.conversationId,
                onBackClick = onNavigateToBack,
            )
        }
    }
}
