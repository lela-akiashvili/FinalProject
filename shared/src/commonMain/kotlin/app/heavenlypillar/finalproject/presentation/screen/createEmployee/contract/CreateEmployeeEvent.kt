package app.heavenlypillar.finalproject.presentation.screen.createEmployee.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType


sealed interface CreateEmployeeEvent {
    data class OnNameChanged(val value: String) : CreateEmployeeEvent
    data class OnLastNameChanged(val value: String) : CreateEmployeeEvent
    data class OnEmailChanged(val value: String) : CreateEmployeeEvent
    data class OnPhoneNumberChanged(val value: String) : CreateEmployeeEvent
    data class OnAddressChanged(val value: String) : CreateEmployeeEvent
    data class OnBirthdayChanged(val value: String) : CreateEmployeeEvent
    data class OnStartDateChanged(val value: String) : CreateEmployeeEvent
    data class OnPositionChanged(val value: String) : CreateEmployeeEvent
    data class OnShiftChanged(val value: String) : CreateEmployeeEvent
    data class OnSalaryChanged(val value: String) : CreateEmployeeEvent
    data object OnCreateClicked : CreateEmployeeEvent
    data object OnDiscardClicked : CreateEmployeeEvent
    data object ToggleBusinessDropdown : CreateEmployeeEvent
    data class OnBusinessSelected(val id: String, val name: String) : CreateEmployeeEvent
    data class OnAvatarSelected(val bytes: ByteArray?) : CreateEmployeeEvent
    data object ToggleRoleDropdown : CreateEmployeeEvent
    data class OnRoleChanged(val role: EmployeeType) : CreateEmployeeEvent
}