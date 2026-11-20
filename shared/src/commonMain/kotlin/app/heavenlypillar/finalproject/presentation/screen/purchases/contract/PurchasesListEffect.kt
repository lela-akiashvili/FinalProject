package app.heavenlypillar.finalproject.presentation.screen.purchases.contract

sealed interface PurchasesListEffect {
    data class ShowSnackbar(val message: String) : PurchasesListEffect
}