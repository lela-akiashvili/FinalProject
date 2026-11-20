package app.heavenlypillar.finalproject.presentation.screen.businessList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.useCases.GetBusinessesUseCase
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.BusinessListEffect
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.BusinessListEvent
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.BusinessListState
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.SortDir
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BusinessListViewModel(
    private val getBusinessesUseCase: GetBusinessesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessListState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<BusinessListEffect>()
    val effects = _effects.asSharedFlow()

    init {
        loadBusinesses()
    }

    fun onEvent(event: BusinessListEvent) {
        when (event) {
            is BusinessListEvent.OnScreenLoad -> loadBusinesses()
            is BusinessListEvent.OnRefresh -> loadBusinesses()
            is BusinessListEvent.OnBusinessClicked -> onBusinessClick(event.businessId)
            is BusinessListEvent.OnSearch -> onSearch(event.searchValue)
            is BusinessListEvent.OnSortChange -> onSortChange(event.sortDir)
            is BusinessListEvent.OnFilterChange -> onFilterChange(event.filterValue)
            is BusinessListEvent.OnToggleBookmark -> onToggleBookmark(event.businessId)
            is BusinessListEvent.OnCreateNewBusiness -> onCreateNewBusiness()
        }
    }

    private fun loadBusinesses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getBusinessesUseCase().fold(
                onSuccess = { list ->
                    _uiState.update { it.copy(isLoading = false, businessList = list) }
                },
                onFailure = { error ->
                    val message = error.message ?: "Failed to load businesses"
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                    _effects.emit(BusinessListEffect.ShowError(message))
                }
            )
        }
    }

    private fun onToggleBookmark(businessId: String) {
        _uiState.update { currentState ->
            val updatedBookmarks = if (businessId in currentState.bookmarkedIds) {
                currentState.bookmarkedIds - businessId
            } else {
                currentState.bookmarkedIds + businessId
            }
            currentState.copy(bookmarkedIds = updatedBookmarks)
        }
    }

    private fun onBusinessClick(businessId: String) = viewModelScope.launch {
        _effects.emit(BusinessListEffect.GoToBusiness(businessId))
    }

    private fun onCreateNewBusiness() = viewModelScope.launch {
        _effects.emit(BusinessListEffect.GoToCreateBusiness)
    }

    private fun onSearch(newValue: String) {
        _uiState.update { it.copy(searchValue = newValue) }
    }

    private fun onSortChange(newValue: SortDir) {
        _uiState.update { it.copy(sortDir = newValue) }
    }

    private fun onFilterChange(newValue: String) {
        _uiState.update { it.copy(filterValue = newValue) }
    }
}