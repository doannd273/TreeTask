package com.treestudio.treetask.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.doannd3.treetask.feature.chat.navigation.ChatGraphRoute
import com.doannd3.treetask.feature.profile.navigation.ProfileGraphRoute
import com.doannd3.treetask.feature.stats.navigation.StatsGraphRoute
import com.doannd3.treetask.feature.tasks.navigation.TasksGraphRoute
import com.treestudio.treetask.R

enum class TopLevelDestination(
    @DrawableRes val selectedIcon: Int,    // Icon khi được bấm
    @DrawableRes val unselectedIcon: Int,  // Icon khi chưa bấm (thường là nét mảnh Outlined)
    @StringRes val titleTextId: Int,  // ID Chuỗi chữ ở dưới
    val route: Any                 // Đường dẫn qua Feature tương ứng
) {
    TASKS(
        selectedIcon = R.drawable.ic_tasks_selected,
        unselectedIcon = R.drawable.ic_tasks_unselected,
        titleTextId = R.string.app_tab_tasks,
        route = TasksGraphRoute
    ),

    CHAT(
        selectedIcon = R.drawable.ic_chat_selected,
        unselectedIcon = R.drawable.ic_chat_unselected,
        titleTextId = R.string.app_tab_chat,
        route = ChatGraphRoute
    ),

    STATS(
        selectedIcon = R.drawable.ic_stats_selected,
        unselectedIcon = R.drawable.ic_stats_unselected,
        titleTextId = R.string.app_tab_stats,
        route = StatsGraphRoute
    ),

    PROFILE(
        selectedIcon = R.drawable.ic_profile_selected,
        unselectedIcon = R.drawable.ic_profile_unselected,
        titleTextId = R.string.app_tab_profile,
        route = ProfileGraphRoute
    ),
}