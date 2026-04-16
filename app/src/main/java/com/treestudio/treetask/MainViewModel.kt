package com.treestudio.treetask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.datastore.TokenManager
import com.doannd3.treetask.feature.auth.navigation.AuthGraphRoute
import com.doannd3.treetask.feature.tasks.navigation.TasksGraphRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    var isLoading by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf<Any?>(null)
        private set

    init {
        viewModelScope.launch {
            val token = tokenManager.getAccessToken().first()
            startDestination = if (token.isNullOrEmpty()) {
                AuthGraphRoute
            } else {
                TasksGraphRoute
            }

            isLoading = false
        }
    }
}