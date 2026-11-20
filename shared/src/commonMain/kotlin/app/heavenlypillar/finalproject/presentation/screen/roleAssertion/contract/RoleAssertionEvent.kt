package app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType

sealed interface RoleAssertionEvent {
    data class OnSelectRole(val role: EmployeeType) : RoleAssertionEvent
}