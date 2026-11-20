package app.heavenlypillar.finalproject.presentation.screen.createEmployee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.repository.StorageRepository
import app.heavenlypillar.finalproject.domain.useCases.CreateEmployeeUseCase
import app.heavenlypillar.finalproject.domain.useCases.GetBusinessesUseCase
import app.heavenlypillar.finalproject.presentation.screen.createEmployee.contract.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CreateEmployeeViewModel(
    private val createEmployeeUseCase: CreateEmployeeUseCase,
    private val getBusinessesUseCase: GetBusinessesUseCase,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEmployeeState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<CreateEmployeeEffect>()
    val effects = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            getBusinessesUseCase().onSuccess { businesses ->
                _uiState.update { it.copy(availableBusinesses = businesses) }
            }
        }
    }

    fun onEvent(event: CreateEmployeeEvent) {
        when (event) {
            is CreateEmployeeEvent.OnNameChanged -> _uiState.update { it.copy(name = event.value) }
            is CreateEmployeeEvent.OnLastNameChanged -> _uiState.update { it.copy(lastName = event.value) }
            is CreateEmployeeEvent.OnEmailChanged -> _uiState.update { it.copy(email = event.value) }
            is CreateEmployeeEvent.OnPhoneNumberChanged -> _uiState.update { it.copy(phoneNumber = event.value) }
            is CreateEmployeeEvent.OnAddressChanged -> _uiState.update { it.copy(address = event.value) }
            is CreateEmployeeEvent.OnBirthdayChanged -> _uiState.update { it.copy(birthday = event.value) }
            is CreateEmployeeEvent.OnStartDateChanged -> _uiState.update { it.copy(startDate = event.value) }
            is CreateEmployeeEvent.OnPositionChanged -> _uiState.update { it.copy(position = event.value) }
            is CreateEmployeeEvent.OnShiftChanged -> _uiState.update { it.copy(shift = event.value) }
            is CreateEmployeeEvent.OnSalaryChanged -> _uiState.update { it.copy(salary = event.value) }
            CreateEmployeeEvent.ToggleRoleDropdown -> _uiState.update { it.copy(isRoleDropdownExpanded = !it.isRoleDropdownExpanded) }
            is CreateEmployeeEvent.OnRoleChanged -> _uiState.update { it.copy(role = event.role, isRoleDropdownExpanded = false) }
            CreateEmployeeEvent.ToggleBusinessDropdown -> _uiState.update { it.copy(isBusinessDropdownExpanded = !it.isBusinessDropdownExpanded) }
            is CreateEmployeeEvent.OnBusinessSelected -> _uiState.update {
                it.copy(selectedBusinessId = event.id, selectedBusinessName = event.name, isBusinessDropdownExpanded = false)
            }

            is CreateEmployeeEvent.OnAvatarSelected -> _uiState.update { it.copy(avatarBytes = event.bytes) }

            CreateEmployeeEvent.OnDiscardClicked -> {
                viewModelScope.launch { _effects.emit(CreateEmployeeEffect.NavigateBack) }
            }
            CreateEmployeeEvent.OnCreateClicked -> handleCreate()
        }
    }

    private fun handleCreate() {
        val state = _uiState.value
        if (state.isLoading) return

        if (state.selectedBusinessId.isBlank()) {
            viewModelScope.launch { _effects.emit(CreateEmployeeEffect.ShowSnackbar("Please select an assigned business.")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            var uploadedPhotoUrl = ""
            if (state.avatarBytes != null) {
                val fileName = "avatars/${Clock.System.now().toEpochMilliseconds()}.jpg"
                uploadedPhotoUrl = storageRepository.uploadImage(state.avatarBytes, fileName).getOrNull() ?: ""
            }

            val employee = Employee(
                name = state.name.trim(),
                lastName = state.lastName.trim(),
                workEmail = state.email.trim(),
                phoneNumber = state.phoneNumber.trim(),
                address = state.address.trim(),
                birthday = state.birthday.trim(),
                startDate = state.startDate.trim(),
                position = state.position.trim(),
                shift = state.shift.trim(),
                salary = state.salary.toFloatOrNull() ?: 0f,
                role = state.role,
                photoUrl = uploadedPhotoUrl,
                assignedBusinessIds = listOf(state.selectedBusinessId),
                businessName = state.selectedBusinessName
            )

            createEmployeeUseCase(employee).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(CreateEmployeeEffect.NavigateBack)
                },
                onFailure = { error ->
                    val msg = error.message ?: "Failed to create employee"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _effects.emit(CreateEmployeeEffect.ShowSnackbar(msg))
                }
            )
        }
    }
}