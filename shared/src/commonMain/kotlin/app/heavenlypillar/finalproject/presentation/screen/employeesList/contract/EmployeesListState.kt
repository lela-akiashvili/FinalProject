package app.heavenlypillar.finalproject.presentation.screen.employeesList.contract

import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.SortDir

data class EmployeesListState(
    val employees: List<Employee> = emptyList(),
    val searchQuery: String = "",
    val sortDir: SortDir = SortDir.Asc,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)