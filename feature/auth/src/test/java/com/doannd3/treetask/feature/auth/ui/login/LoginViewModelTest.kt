package com.doannd3.treetask.feature.auth.ui.login

import androidx.lifecycle.viewmodel.compose.viewModel
import com.doannd3.treetask.core.domain.usecase.auth.LoginUseCase
import com.doannd3.treetask.core.domain.usecase.device.RegisterCurrentDeviceTokenUseCase
import com.doannd3.treetask.core.testing.util.MainDispatcherRule
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule

class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val loginUseCase: LoginUseCase = mockk()
    private val registerCurrentDeviceTokenUseCase: RegisterCurrentDeviceTokenUseCase = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel =
            LoginViewModel(
                loginUseCase = loginUseCase,
                registerCurrentDeviceTokenUseCase = registerCurrentDeviceTokenUseCase,
            )
    }
}
