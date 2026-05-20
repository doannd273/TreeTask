package com.doannd3.treetask.feature.auth.ui.register

import com.doannd3.treetask.core.domain.usecase.auth.RegisterUseCase
import com.doannd3.treetask.core.testing.util.MainDispatcherRule
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule

class RegisterViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val registerUseCase: RegisterUseCase = mockk()
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        viewModel = RegisterViewModel(
            registerUseCase = registerUseCase,
        )
    }
}
