package app.heavenlypillar.finalproject.presentation.screen.businessList.contract

import app.heavenlypillar.finalproject.domain.model.Business

sealed interface BusinessListEvent{
    data object OnScreenLoad : BusinessListEvent
    data object OnRefresh : BusinessListEvent
    data object OnCreateNewBusiness : BusinessListEvent
    data class OnSearch(val searchValue: String) : BusinessListEvent
    data class OnFilterChange(val filterValue: String) : BusinessListEvent
    data class OnSortChange(val sortDir: SortDir) : BusinessListEvent
    data class OnToggleBookmark(val businessId: String) : BusinessListEvent
    data class OnBusinessClicked(val businessId: String) : BusinessListEvent
}