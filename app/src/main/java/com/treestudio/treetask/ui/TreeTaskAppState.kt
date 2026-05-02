package com.treestudio.treetask.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.treestudio.treetask.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberTreeTaskAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): TreeTaskAppState {
    return remember(navController, coroutineScope) {
        TreeTaskAppState(
            navController = navController,
            coroutineScope = coroutineScope,
        )
    }
}

@Stable
class TreeTaskAppState(
    val navController: NavHostController,
    val coroutineScope: CoroutineScope,
) {
    // --- 2. TRẠNG THÁI HIỆN TẠI (LÝ TƯỞNG CHO BOTTOM NAVIGATION BAR) ---
    // Giúp App biết User đang ở màn nào để bật sáng cái Icon dưới Bottom Bar tương ứng
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    // (Chỉ hiện Bottom Bar ở các màn hình chính)
    val isTopLevelDestination: Boolean
        @Composable get() = currentDestination?.let { destination ->
            TopLevelDestination.entries.any { topLevelDest ->
                // BẮT BUỘC PHẢI QUÉT GIA PHẢ BẰNG VÒNG LẶP NÀY
                destination.hierarchy.any { navDest ->

                    if (topLevelDest.route is String) {
                        navDest.route == topLevelDest.route
                    } else {
                        // So sánh thành công khi lặp tới trúng Lớp Cha (TasksGraphRoute)
                        navDest.route?.contains(
                            topLevelDest.route::class.qualifiedName ?: "",
                        ) == true
                    }
                }
            }
        } ?: false

    // --- 3. LOGIC CHUYỂN TRANG VĨ MÔ (BOTTOM TABS) ---
    // Các lệnh chuyển trang nhỏ lẻ (Ví dụ: Từ List qua Detail) thì để Feature Modules tự lo.
    // Lệnh chuyển trang khổng lồ (Ví dụ: Bấm tab Home qua Tab Setting) sẽ do AppState gánh.
    fun navigateToTopLevelDestination(route: Any) {
        navController.navigate(route) {
            // 1. PopUpTo đầu nguồn để ko bị xếp chồng màn hình khi lướt tab
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            // 2. Chônống mở đúp 1 màn 2 lần
            launchSingleTop = true
            // 3. Tự phục hồi lại lướt cuộn cũ nếu quay lại tab
            restoreState = true
        }
    }
}
