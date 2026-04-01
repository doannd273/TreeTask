package com.treestudio.treetask.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.doannd3.treetask.feature.chat.navigation.chatGraphRoutePattern
import com.doannd3.treetask.feature.profile.navigation.profileGraphRoutePattern
import com.doannd3.treetask.feature.stats.navigation.statsGraphRoutePattern
import com.doannd3.treetask.feature.tasks.navigation.tasksGraphRoutePattern
import com.treestudio.treetask.R

enum class TopLevelDestination(
    @DrawableRes val selectedIcon: Int,    // Icon khi được bấm
    @DrawableRes val unselectedIcon: Int,  // Icon khi chưa bấm (thường là nét mảnh Outlined)
    @StringRes val titleTextId: Int,  // ID Chuỗi chữ ở dưới
    val route: String                 // Đường dẫn qua Feature tương ứng
) {
    // Ví dụ 2 Tab chính:
    TASKS(
        selectedIcon = R.drawable.ic_tasks_selected,
        unselectedIcon = R.drawable.ic_tasks_unselected,
        titleTextId = R.string.app_tab_tasks,
        route = tasksGraphRoutePattern
    ),
    PROFILE(
        selectedIcon = R.drawable.ic_profile_selected,
        unselectedIcon = R.drawable.ic_profile_unselected,
        titleTextId = R.string.app_tab_profile,
        route = profileGraphRoutePattern
    ),

    STATS(
        selectedIcon = R.drawable.ic_stats_selected,
        unselectedIcon = R.drawable.ic_stats_unselected,
        titleTextId = R.string.app_tab_stats,
        route = statsGraphRoutePattern
    ),

    CHAT(
        selectedIcon = R.drawable.ic_chat_selected,
        unselectedIcon = R.drawable.ic_chat_unselected,
        titleTextId = R.string.app_tab_chat,
        route = chatGraphRoutePattern
    )
}