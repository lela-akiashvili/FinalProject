package app.heavenlypillar.finalproject.presentation.screen.purchases.contract

import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.model.PurchaseOrder
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.SortDir

enum class PurchaseTab {
    PendingOrders, // Manager
    Approved,      // Manager
    PastOrders,    // Both
    CreateNewOrder // RegEmployee
}

data class PurchasesListState(
    val role: EmployeeType = EmployeeType.RegEmployee,
    val selectedTab: PurchaseTab = PurchaseTab.CreateNewOrder,
    val orders: List<PurchaseOrder> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isInitialized: Boolean = false,

    val formSubject: String = "",
    val formType: String = "",
    val formAmount: String = "",
    val formReason: String = "",
    val formApprover: String = "",

    // Dropdown Data
    val isTypeDropdownExpanded: Boolean = false,
    val isApproverDropdownExpanded: Boolean = false,
    val availableApprovers: List<String> = emptyList(),
    val availableTypes: List<String> = listOf("Beverage", "Snack", "Vegetable", "Fish", "Infrastructure", "Furniture", "Other")
)