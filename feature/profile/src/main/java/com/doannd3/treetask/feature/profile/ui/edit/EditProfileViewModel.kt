package com.doannd3.treetask.feature.profile.ui.edit

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
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

@HiltViewModel
class EditProfileViewModel
@Inject
constructor() :
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

            is EditProfileEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is EditProfileEvent.PhoneChanged -> {
                _uiState.update { it.copy(phone = event.phone) }
            }

            EditProfileEvent.SaveClicked -> {
                submitEditProfile()
            }

            EditProfileEvent.BackClicked -> {
                navigateBack()
            }
        }
    }

    private fun submitEditProfile() {
        if (_uiState.value.isLoading) {
            return
        }

        navigateBack()
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
