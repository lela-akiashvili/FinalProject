package app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType

sealed interface RoleAssertionEffect{
    data class NavigateToLogin(val role: EmployeeType) : RoleAssertionEffect
}