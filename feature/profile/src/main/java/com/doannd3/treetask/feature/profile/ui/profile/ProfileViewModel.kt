package com.doannd3.treetask.feature.profile.ui.profile

import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel(),
    MviViewModel<ProfileState, ProfileEvent, ProfileEffect> {

    private val _uiState = MutableStateFlow(ProfileState())
    override val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    override val effect: SharedFlow<ProfileEffect> = _effect.asSharedFlow()

    override fun onEvent(event: ProfileEvent) {

    }
}