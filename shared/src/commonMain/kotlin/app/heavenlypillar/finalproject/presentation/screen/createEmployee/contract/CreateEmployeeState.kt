package app.heavenlypillar.finalproject.presentation.screen.createEmployee.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType

data class CreateEmployeeState(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val birthday: String = "",
    val startDate: String = "",
    val position: String = "",
    val shift: String = "",
    val salary: String = "",
    val role: EmployeeType = EmployeeType.RegEmployee,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableBusinesses: List<app.heavenlypillar.finalproject.domain.model.Business> = emptyList(),
    val selectedBusinessId: String = "",
    val selectedBusinessName: String = "",
    val isBusinessDropdownExpanded: Boolean = false,
    val avatarBytes: ByteArray? = null,
    val isEditing: Boolean = true,
    val isRoleDropdownExpanded: Boolean = false
)