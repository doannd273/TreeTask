package com.treestudio.treetask.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.doannd3.treetask.feature.chat.navigation.ChatGraphDestination
import com.doannd3.treetask.feature.profile.navigation.ProfileGraphDestination
import com.doannd3.treetask.feature.stats.navigation.StatsGraphDestination
import com.doannd3.treetask.feature.tasks.navigation.TasksGraphDestination
import com.treestudio.treetask.R

enum class TopLevelDestination(
    @DrawableRes val selectedIcon: Int,
    @DrawableRes val unselectedIcon: Int,
    @StringRes val titleTextId: Int,
    val route: Any,
) {
    TASKS(
        selectedIcon = R.drawable.ic_tasks_selected,
        unselectedIcon = R.drawable.ic_tasks_unselected,
        titleTextId = R.string.app_tab_tasks,
        route = TasksGraphDestination,
    ),

    CHAT(
        selectedIcon = R.drawable.ic_chat_selected,
        unselectedIcon = R.drawable.ic_chat_unselected,
        titleTextId = R.string.app_tab_chat,
        route = ChatGraphDestination,
    ),

    STATS(
        selectedIcon = R.drawable.ic_stats_selected,
        unselectedIcon = R.drawable.ic_stats_unselected,
        titleTextId = R.string.app_tab_stats,
        route = StatsGraphDestination,
    ),

    PROFILE(
        selectedIcon = R.drawable.ic_profile_selected,
        unselectedIcon = R.drawable.ic_profile_unselected,
        titleTextId = R.string.app_tab_profile,
        route = ProfileGraphDestination,
    ),
}
