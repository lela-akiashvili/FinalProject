package app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType

data class RoleAssertionState(
    val selectedRole: EmployeeType? = null,
    val isLoading: Boolean = false
)