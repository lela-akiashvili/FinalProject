package app.heavenlypillar.finalproject.presentation.screen.login.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType

data class LoginState(
    val email: String = "",
    val password: String = "",
    val role: EmployeeType = EmployeeType.RegEmployee,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)