package app.heavenlypillar.finalproject.presentation.navigation

import app.heavenlypillar.finalproject.domain.model.EmployeeType

sealed interface AppDestination {
    data object Loading : AppDestination
    data object RoleAssertion : AppDestination
    data object CreateBusiness : AppDestination

    data class Login(val role: EmployeeType) : AppDestination
    data class BusinessList(val role: EmployeeType) : AppDestination

//    data object Calendar : AppDestination
    data object Employees : AppDestination
    data object Purchases : AppDestination
    data object Profile : AppDestination
}