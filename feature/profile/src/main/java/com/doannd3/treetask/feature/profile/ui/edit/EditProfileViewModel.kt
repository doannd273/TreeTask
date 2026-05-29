package com.doannd3.treetask.feature.profile.ui.edit

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.user.ObserveCurrentUserUseCase
import com.doannd3.treetask.core.domain.usecase.user.UpdateProfileUseCase
import com.doannd3.treetask.core.domain.usecase.user.UploadAvatarUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
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
        private val userUseCase: ObserveCurrentUserUseCase,
        private val uploadAvatarUseCase: UploadAvatarUseCase,
    ) : BaseViewModel(),
        MviViewModel<EditProfileState, EditProfileEvent, EditProfileEffect> {
        private val _uiState = MutableStateFlow(EditProfileState())
        override val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<EditProfileEffect>()
        override val effect: SharedFlow<EditProfileEffect> = _effect.asSharedFlow()

        init {
            executeSafe {
                val user = userUseCase.invoke().firstOrNull()
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            email = user.email,
                            fullName = user.fullName,
                            avatarUrl = user.avatar ?: "",
                            phone = user.phone ?: "",
                        )
                    }
                }
            }
        }

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

                is EditProfileEvent.AvatarChanged -> {
                    _uiState.update { it.copy(avatarUri = event.avatarUri) }
                }
            }
        }

        private fun submitEditProfile() {
            val state = _uiState.value
            if (state.isLoading) return

            executeSafe {
                _uiState.update { it.copy(isLoading = true) }

                // Bước 1: upload avatar nếu có
                val avatarUrl =
                    if (state.avatarUri != null) {
                        when (val upload = uploadAvatarUseCase(uri = state.avatarUri)) {
                            is ApiResult.Success -> {
                                val url = upload.data
                                if (url.isNullOrBlank()) {
                                    _uiState.update { it.copy(isLoading = false) }
                                    _effect.emit(
                                        EditProfileEffect.ShowErrorMessage(
                                            UiText.StringResource(CommonR.string.common_error_unknown),
                                        ),
                                    )
                                    return@executeSafe
                                }
                                url
                            }
                            is ApiResult.Error -> {
                                _uiState.update { it.copy(isLoading = false) }
                                _effect.emit(
                                    EditProfileEffect.ShowErrorMessage(
                                        upload.toDisplayMessage(UiText.StringResource(CommonR.string.common_error_unknown)),
                                    ),
                                )
                                return@executeSafe
                            }
                        }
                    } else {
                        state.avatarUrl // giữ nguyên URL cũ
                    }

                // Bước 2: update profile
                val result =
                    updateProfileUseCase(
                        fullName = state.fullName,
                        phone = state.phone,
                        avatar = avatarUrl,
                    )
                _uiState.update { it.copy(isLoading = false) }

                when (result) {
                    is ApiResult.Success -> {
                        val message =
                            result.message
                                ?: UiText.StringResource(ProfileR.string.profile_edit_update_successfully)
                        _effect.emit(EditProfileEffect.ShowSuccessMessage(message))
                    }
                    is ApiResult.Error -> {
                        _effect.emit(
                            EditProfileEffect.ShowErrorMessage(
                                result.toDisplayMessage(UiText.StringResource(CommonR.string.common_error_unknown)),
                            ),
                        )
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
