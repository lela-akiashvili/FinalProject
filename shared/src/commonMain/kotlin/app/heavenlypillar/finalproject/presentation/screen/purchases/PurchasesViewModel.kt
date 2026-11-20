package app.heavenlypillar.finalproject.presentation.screen.purchases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.EmployeeRepository
import app.heavenlypillar.finalproject.domain.useCases.GetPurchasesUseCase
import app.heavenlypillar.finalproject.domain.useCases.ManagePurchaseUseCase
import app.heavenlypillar.finalproject.presentation.screen.purchases.contract.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PurchasesViewModel(
    private val getPurchasesUseCase: GetPurchasesUseCase,
    private val managePurchaseUseCase: ManagePurchaseUseCase,
    private val authRepository: AuthRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchasesListState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<PurchasesListEffect>()
    val effects = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            val emp = authRepository.getCurrentEmployee()
            val role = emp?.role ?: EmployeeType.RegEmployee
            val initialTab = if (role == EmployeeType.Manager) PurchaseTab.PendingOrders else PurchaseTab.CreateNewOrder

            val approversList = if (emp != null) {
                employeeRepository.getEmployeesByBusiness(emp.assignedBusinessIds).getOrNull()
                    ?.filter { it.role == EmployeeType.Manager }
                    ?.map { "${it.name} ${it.lastName}" }
                    ?: emptyList()
            } else emptyList()

            _uiState.update {
                it.copy(
                    role = role,
                    selectedTab = initialTab,
                    isInitialized = true,
                    availableApprovers = approversList
                )
            }
            onEvent(PurchasesListEvent.LoadPurchases)
        }
    }

    fun onEvent(event: PurchasesListEvent) {
        when (event) {
            PurchasesListEvent.LoadPurchases -> fetchOrders()
            is PurchasesListEvent.SelectTab -> _uiState.update { it.copy(selectedTab = event.tab) }
            is PurchasesListEvent.OnSearch -> _uiState.update { it.copy(searchQuery = event.query) }
            is PurchasesListEvent.OnSubjectChanged -> _uiState.update { it.copy(formSubject = event.v) }
            is PurchasesListEvent.OnTypeChanged -> _uiState.update { it.copy(formType = event.v, isTypeDropdownExpanded = false) }
            is PurchasesListEvent.OnAmountChanged -> _uiState.update { it.copy(formAmount = event.v) }
            is PurchasesListEvent.OnReasonChanged -> _uiState.update { it.copy(formReason = event.v) }
            is PurchasesListEvent.OnApproverChanged -> _uiState.update { it.copy(formApprover = event.v, isApproverDropdownExpanded = false) }
            PurchasesListEvent.ToggleTypeDropdown -> _uiState.update { it.copy(isTypeDropdownExpanded = !it.isTypeDropdownExpanded) }
            PurchasesListEvent.ToggleApproverDropdown -> _uiState.update { it.copy(isApproverDropdownExpanded = !it.isApproverDropdownExpanded) }
            PurchasesListEvent.DiscardOrder -> clearForm()
            PurchasesListEvent.SubmitOrder -> submitOrder()
            is PurchasesListEvent.UpdateOrderStatus -> changeStatus(event.orderId, event.status)
        }
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getPurchasesUseCase().fold(
                onSuccess = { list -> _uiState.update { it.copy(orders = list, isLoading = false) } },
                onFailure = { _uiState.update { it.copy(isLoading = false) } }
            )
        }
    }

    private fun submitOrder() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            managePurchaseUseCase.createOrder(s.formSubject, s.formType, s.formAmount, s.formReason, s.formApprover)
                .fold(
                    onSuccess = {
                        clearForm()
                        _effects.emit(PurchasesListEffect.ShowSnackbar("Order created successfully!"))
                        fetchOrders()
                        _uiState.update { state -> state.copy(selectedTab = PurchaseTab.PastOrders) }
                    },
                    onFailure = {
                        _effects.emit(PurchasesListEffect.ShowSnackbar(it.message ?: "Failed to create order"))
                        _uiState.update { state -> state.copy(isLoading = false) }
                    }
                )
        }
    }

    private fun changeStatus(orderId: String, status: app.heavenlypillar.finalproject.domain.model.OrderStatus) {
        viewModelScope.launch {
            managePurchaseUseCase.updateStatus(orderId, status).onSuccess {
                fetchOrders()
            }
        }
    }

    private fun clearForm() {
        _uiState.update {
            it.copy(formSubject = "", formType = "", formAmount = "", formReason = "", formApprover = "")
        }
    }
}