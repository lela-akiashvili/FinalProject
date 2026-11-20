package app.heavenlypillar.finalproject.presentation.screen.createEmployee.contract

sealed interface CreateEmployeeEffect {
    data object NavigateBack : CreateEmployeeEffect
    data class ShowSnackbar(val message: String) : CreateEmployeeEffect
}