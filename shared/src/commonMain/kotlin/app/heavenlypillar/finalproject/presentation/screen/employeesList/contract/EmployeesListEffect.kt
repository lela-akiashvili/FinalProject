package app.heavenlypillar.finalproject.presentation.screen.employeesList.contract


sealed interface EmployeesListEffect {
    data object NavigateToCreateEmployee : EmployeesListEffect
    data class NavigateToEmployeeDetails(val employeeId: String) : EmployeesListEffect
    data class ShowSnackbar(val message: String) : EmployeesListEffect
}