package app.heavenlypillar.finalproject.presentation.screen.login.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType

sealed interface LoginEffect {
    data class NavigateToBusinesses(val role: EmployeeType) : LoginEffect
    data class ShowSnackbar(val message: String) : LoginEffect
}