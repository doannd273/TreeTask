package com.doannd3.treetask.core.common.dispatcher

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(
    val treeTaskDispatcher: TreeTaskDispatchers,
)

enum class TreeTaskDispatchers {
    Default,
    IO,
    Main,
}
