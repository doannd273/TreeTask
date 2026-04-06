package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.doannd3.treetask.feature.auth.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordRoute(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onForgotPasswordBack: () -> Unit
) {

}