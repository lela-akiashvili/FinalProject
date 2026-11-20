package app.heavenlypillar.finalproject.presentation.screen.purchases.contract

import app.heavenlypillar.finalproject.domain.model.OrderStatus

sealed interface PurchasesListEvent {
    data object LoadPurchases : PurchasesListEvent
    data class SelectTab(val tab: PurchaseTab) : PurchasesListEvent
    data class OnSearch(val query: String) : PurchasesListEvent

    data class OnSubjectChanged(val v: String) : PurchasesListEvent
    data class OnTypeChanged(val v: String) : PurchasesListEvent
    data class OnAmountChanged(val v: String) : PurchasesListEvent
    data class OnReasonChanged(val v: String) : PurchasesListEvent
    data class OnApproverChanged(val v: String) : PurchasesListEvent
    data object ToggleTypeDropdown : PurchasesListEvent
    data object ToggleApproverDropdown : PurchasesListEvent

    data object DiscardOrder : PurchasesListEvent
    data object SubmitOrder : PurchasesListEvent
    data class UpdateOrderStatus(val orderId: String, val status: OrderStatus) : PurchasesListEvent
}