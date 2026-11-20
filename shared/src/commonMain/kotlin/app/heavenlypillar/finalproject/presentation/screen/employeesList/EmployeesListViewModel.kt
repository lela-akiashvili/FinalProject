package app.heavenlypillar.finalproject.presentation.screen.employeesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.useCases.GetEmployeesUseCase
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.SortDir
import app.heavenlypillar.finalproject.presentation.screen.employeesList.contract.EmployeesListEffect
import app.heavenlypillar.finalproject.presentation.screen.employeesList.contract.EmployeesListEvent
import app.heavenlypillar.finalproject.presentation.screen.employeesList.contract.EmployeesListState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmployeesListViewModel(
    private val getEmployeesUseCase: GetEmployeesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeesListState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<EmployeesListEffect>()
    val effects = _effects.asSharedFlow()

    init {
        loadEmployees()
    }

    fun onEvent(event: EmployeesListEvent) {
        when (event) {
            EmployeesListEvent.OnRefresh -> loadEmployees()
            is EmployeesListEvent.OnSearch -> _uiState.update { it.copy(searchQuery = event.query) }
            is EmployeesListEvent.OnSortChange -> _uiState.update { it.copy(sortDir = event.sortDir) }
            is EmployeesListEvent.OnEmployeeClicked -> {
                viewModelScope.launch { _effects.emit(EmployeesListEffect.NavigateToEmployeeDetails(event.employeeId)) }
            }
            EmployeesListEvent.OnCreateEmployeeClicked -> {
                viewModelScope.launch { _effects.emit(EmployeesListEffect.NavigateToCreateEmployee) }
            }
        }
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getEmployeesUseCase().fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(isLoading = false, employees = list) }
                },
                onFailure = { error ->
                    val msg = error.message ?: "Failed to load employees"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _effects.emit(EmployeesListEffect.ShowSnackbar(msg))
                }
            )
        }
    }
}