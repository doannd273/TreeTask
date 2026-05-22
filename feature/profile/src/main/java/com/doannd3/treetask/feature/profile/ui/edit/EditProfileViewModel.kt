package com.doannd3.treetask.feature.profile.ui.edit

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.user.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.doannd3.treetask.core.common.R as CommonR
import com.doannd3.treetask.feature.profile.R as ProfileR

@HiltViewModel
class EditProfileViewModel
@Inject
constructor(
    private val updateProfileUseCase: UpdateProfileUseCase,
) :
    BaseViewModel(),
    MviViewModel<EditProfileState, EditProfileEvent, EditProfileEffect> {
    private val _uiState = MutableStateFlow(EditProfileState())
    override val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<EditProfileEffect>()
    override val effect: SharedFlow<EditProfileEffect> = _effect.asSharedFlow()

    override fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.FullNameChanged -> {
                _uiState.update { it.copy(fullName = event.fullName) }
            }

            is EditProfileEvent.PhoneChanged -> {
                _uiState.update { it.copy(phone = event.phone) }
            }

            EditProfileEvent.SubmitEditProfile -> {
                submitEditProfile()
            }

            EditProfileEvent.BackClicked -> {
                navigateBack()
            }

            EditProfileEvent.SuccessAcknowledged -> {
                navigateBack()
            }
        }
    }

    private fun submitEditProfile() {
        val state = _uiState.value
        if (state.isLoading) {
            return
        }

        executeSafe {
            _uiState.update { it.copy(isLoading = true) }
            val result =
                updateProfileUseCase(
                    fullName = state.fullName,
                    phone = state.phone,
                    avatar = state.avatarUrl,
                )
            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    val message =
                        result.message
                            ?: UiText.StringResource(ProfileR.string.profile_update_profile_success)
                    _effect.emit(EditProfileEffect.ShowSuccessMessage(message))
                }

                is ApiResult.Error -> {
                    val message =
                        result.toDisplayMessage(
                            UiText.StringResource(CommonR.string.common_error_unknown),
                        )
                    _effect.emit(EditProfileEffect.ShowErrorMessage(message))
                }
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.emit(EditProfileEffect.NavigateBack)
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
