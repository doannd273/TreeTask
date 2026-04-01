package com.treestudio.treetask.ui.component

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.treestudio.treetask.navigation.TopLevelDestination

@Composable
fun TreeTaskBottomBar(
    destinations: List<TopLevelDestination>, // Truyền trọn bộ Enum vào
    currentDestination: NavDestination?,     // Báo cho UI biết đang đứng ở đâu để sáng đèn lên
    onNavigateToDestination: (TopLevelDestination) -> Unit, // Callback khi quẹt tab
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
        // Bạn có thể đè thêm containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp) nếu thích
    ) {
        // Lôi từng Menu ra vẽ
        destinations.forEach { destination ->

            // LOGIC CỰC HAY: Bật sáng đèn Icon dù người dùng đang ở Màn Hình Con (Detail) sâu bên trong
            // Miễn là màn con đó chung 1 phả hệ (hierarchy) với Graph bọc ngoài của Tab
            val selected = currentDestination?.hierarchy?.any {
                it.route == destination.route
            } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigateToDestination(destination) },
                icon = {
                    Icon(
                        painter = painterResource(if (selected) destination.selectedIcon else destination.unselectedIcon),
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(destination.titleTextId)) },
                alwaysShowLabel = true // Set false nếu app quá đông 5 Tabs
            )
        }
    }
}