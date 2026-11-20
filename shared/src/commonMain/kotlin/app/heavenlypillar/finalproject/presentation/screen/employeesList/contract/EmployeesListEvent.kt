package app.heavenlypillar.finalproject.presentation.screen.employeesList.contract

import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.SortDir

sealed interface EmployeesListEvent {
    data object OnRefresh : EmployeesListEvent
    data class OnSearch(val query: String) : EmployeesListEvent
    data class OnSortChange(val sortDir: SortDir) : EmployeesListEvent
    data class OnEmployeeClicked(val employeeId: String) : EmployeesListEvent
    data object OnCreateEmployeeClicked : EmployeesListEvent
}