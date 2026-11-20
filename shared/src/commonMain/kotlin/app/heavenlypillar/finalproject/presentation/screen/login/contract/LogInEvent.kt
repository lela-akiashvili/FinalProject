package app.heavenlypillar.finalproject.presentation.screen.login.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType

sealed interface LoginEvent  {
    data class SetTargetRole(val role: EmployeeType) : LoginEvent
    data class OnEmailChanged(val email: String) : LoginEvent
    data class OnPasswordChanged(val password: String) : LoginEvent
    data object OnSignInClicked : LoginEvent
    data object OnClearError : LoginEvent
}