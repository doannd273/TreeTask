package com.doannd3.treetask.feature.chat.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.navigation

const val chatGraphRoutePattern = "chat_graph"
const val chatRoute = "chat_route"

fun NavController.navigateToChatGraph(navOptions: NavOptions? = null) {
    this.navigate(route = chatGraphRoutePattern, navOptions = navOptions)
}

fun NavGraphBuilder.chatGraph() {
    navigation(
        route = chatGraphRoutePattern,
        startDestination = chatRoute
    ) {

    }
}