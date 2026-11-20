package app.heavenlypillar.finalproject.presentation.screen.businessForm.contract

sealed interface BusinessFormEffect {
    data object NavigateBack : BusinessFormEffect
    data class ShowSnackbar(val message: String) : BusinessFormEffect
}