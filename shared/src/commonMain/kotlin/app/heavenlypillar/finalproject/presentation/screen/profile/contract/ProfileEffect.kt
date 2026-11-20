package app.heavenlypillar.finalproject.presentation.screen.profile.contract

sealed interface ProfileEffect {
    data object NavigateBack : ProfileEffect
    data class ShowSnackbar(val message: String) : ProfileEffect
    data object NavigateToLogin : ProfileEffect
}