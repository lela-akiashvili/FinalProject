package app.heavenlypillar.finalproject.presentation.screen.businessList.contract

import app.heavenlypillar.finalproject.domain.model.Business

data class BusinessListState(
    val businessList: List<Business> = emptyList(),
    val searchValue: String = "",
    val filterValue: String = "ALL",
    val sortDir: SortDir = SortDir.Asc,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bookmarkedIds: Set<String> = emptySet()
)

enum class SortDir{
    Asc,
    Desc
}