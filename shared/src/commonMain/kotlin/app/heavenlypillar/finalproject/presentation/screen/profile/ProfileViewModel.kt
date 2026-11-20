package app.heavenlypillar.finalproject.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.model.VacationType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.StorageRepository
import app.heavenlypillar.finalproject.domain.repository.VacationRepository
import app.heavenlypillar.finalproject.domain.useCases.*
import app.heavenlypillar.finalproject.presentation.screen.profile.contract.*
import io.ktor.util.encodeBase64
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val requestVacationUseCase: RequestVacationUseCase,
    private val terminateEmployeeUseCase: TerminateEmployeeUseCase,
    private val authRepository: AuthRepository,
    private val vacationRepository: VacationRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ProfileEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile(event.targetEmployeeId)
            is ProfileEvent.SelectTab -> _uiState.update { it.copy(selectedTab = event.tab) }
            ProfileEvent.ToggleEditing -> _uiState.update { it.copy(isEditing = !it.isEditing) }
            is ProfileEvent.OnNameChanged -> _uiState.update { it.copy(name = event.value) }
            is ProfileEvent.OnLastNameChanged -> _uiState.update { it.copy(lastName = event.value) }
            is ProfileEvent.OnAddressChanged -> _uiState.update { it.copy(address = event.value) }
            is ProfileEvent.OnPhoneChanged -> _uiState.update { it.copy(phoneNumber = event.value) }
            is ProfileEvent.OnBirthdayChanged -> _uiState.update { it.copy(birthday = event.value) }
            is ProfileEvent.OnLanguageChanged -> _uiState.update { it.copy(preferredLanguage = event.value) }
            ProfileEvent.DiscardProfileClicked -> resetForm()
            ProfileEvent.SaveProfileClicked -> saveProfile()
            is ProfileEvent.OnAvatarSelected -> _uiState.update { it.copy(avatarBytes = event.bytes) }
            ProfileEvent.OpenChangePasswordDialog -> _uiState.update { it.copy(isChangePasswordDialogOpen = true) }
            ProfileEvent.CloseChangePasswordDialog -> _uiState.update { it.copy(isChangePasswordDialogOpen = false) }
            is ProfileEvent.SubmitNewPassword -> changePassword(event.newPassword)
            ProfileEvent.OpenAddVacationDialog -> _uiState.update { it.copy(isAddVacationDialogOpen = true) }
            ProfileEvent.CloseAddVacationDialog -> _uiState.update { it.copy(isAddVacationDialogOpen = false) }
            is ProfileEvent.SubmitVacationRequest -> submitVacation(event.type, event.startDate, event.endDate, event.days)
            ProfileEvent.OpenTerminationDialog -> _uiState.update { it.copy(isTerminationConfirmDialogOpen = true) }
            ProfileEvent.CloseTerminationDialog -> _uiState.update { it.copy(isTerminationConfirmDialogOpen = false) }
            ProfileEvent.ConfirmTermination -> terminateAccount()
            ProfileEvent.SignOutClicked -> signOut()
        }
    }

    private fun loadProfile(targetId: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getProfileUseCase(targetId).fold(
                onSuccess = { profileData ->
                    val emp = profileData.targetEmployee
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            viewerRole = profileData.currentViewer.role,
                            isOwnProfile = profileData.isOwnProfile,
                            targetEmployee = emp,
                            name = emp.name,
                            lastName = emp.lastName,
                            email = emp.workEmail,
                            address = emp.address,
                            startDate = emp.startDate,
                            phoneNumber = emp.phoneNumber,
                            birthday = emp.birthday,
                            ptoDaysLeft = emp.dayOffLeft,
                            selectedTab = ProfileTab.Details
                        )
                    }
                    if (profileData.isOwnProfile) {
                        loadVacations(emp.id)
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    private fun loadVacations(employeeId: String) {
        viewModelScope.launch {
            vacationRepository.getVacationsForEmployee(employeeId).onSuccess { list ->
                _uiState.update { it.copy(vacations = list) }
            }
        }
    }

    private fun resetForm() {
        val emp = _uiState.value.targetEmployee ?: return
        _uiState.update {
            it.copy(
                isEditing = false,
                name = emp.name,
                lastName = emp.lastName,
                address = emp.address,
                phoneNumber = emp.phoneNumber,
                birthday = emp.birthday
            )
        }
    }

    private fun changePassword(newPass: String) {
        viewModelScope.launch {
            authRepository.updatePassword(newPass).fold(
                onSuccess = {
                    _uiState.update { it.copy(isChangePasswordDialogOpen = false) }
                    _effects.emit(ProfileEffect.ShowSnackbar("Password changed successfully"))
                },
                onFailure = { _effects.emit(ProfileEffect.ShowSnackbar("Password change failed: ${it.message}")) }
            )
        }
    }

    private fun submitVacation(type: VacationType, start: String, end: String, days: Int) {
        viewModelScope.launch {
            requestVacationUseCase(type, start, end, days).fold(
                onSuccess = {
                    _uiState.update { it.copy(isAddVacationDialogOpen = false) }
                    _effects.emit(ProfileEffect.ShowSnackbar("Request submitted"))
                    _uiState.value.targetEmployee?.let { loadVacations(it.id) }
                },
                onFailure = { _effects.emit(ProfileEffect.ShowSnackbar(it.message ?: "Failed to submit request")) }
            )
        }
    }

    private fun terminateAccount() {
        val targetId = _uiState.value.targetEmployee?.id ?: return
        viewModelScope.launch {
            terminateEmployeeUseCase(targetId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isTerminationConfirmDialogOpen = false) }
                    _effects.emit(ProfileEffect.ShowSnackbar("Account terminated successfully"))
                    _effects.emit(ProfileEffect.NavigateBack)
                },
                onFailure = { _effects.emit(ProfileEffect.ShowSnackbar("Termination failed: ${it.message}")) }
            )
        }
    }
    private fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _effects.emit(ProfileEffect.NavigateToLogin)
        }
    }

    private fun saveProfile() {
        val s = _uiState.value
        val current = s.targetEmployee ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val uploadedPhotoUrl = if (s.avatarBytes != null) {
                s.avatarBytes.encodeBase64()
            } else {
                current.photoUrl
            }

            val updated = current.copy(
                name = s.name,
                lastName = s.lastName,
                address = s.address,
                phoneNumber = s.phoneNumber,
                birthday = s.birthday,
                photoUrl = uploadedPhotoUrl
            )
            authRepository.updateEmployeeProfile(updated).fold(
                onSuccess = {
                    _uiState.update { it.copy(isEditing = false, targetEmployee = updated, avatarBytes = null, isLoading = false) }
                    _effects.emit(ProfileEffect.ShowSnackbar("Profile updated successfully"))
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(ProfileEffect.ShowSnackbar("Failed to update profile"))
                }
            )
        }
    }
}