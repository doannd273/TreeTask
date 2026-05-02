package com.treestudio.treetask.ui.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.treestudio.treetask.navigation.TopLevelDestination

@Composable
fun TreeTaskBottomBar(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .height(56.dp),
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        // Lôi từng Menu ra vẽ
        destinations.forEach { destination ->

            // LOGIC CỰC HAY: Bật sáng đèn Icon dù người dùng đang ở Màn Hình Con (Detail) sâu bên trong
            // Miễn là màn con đó chung 1 phả hệ (hierarchy) với Graph bọc ngoài của Tab
            val selected = currentDestination?.hierarchy?.any { navDest ->
                if (destination.route is String) {
                    navDest.route == destination.route
                } else {
                    navDest.route?.contains(destination.route::class.qualifiedName ?: "") == true
                }
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        painter = painterResource(if (selected) destination.selectedIcon else destination.unselectedIcon),
                        contentDescription = null,
                    )
                },
                /*  label = { Text(stringResource(destination.titleTextId)) },
                  alwaysShowLabel = true // Set false nếu app quá đông 5 Tabs*/
            )
        }
    }
}
