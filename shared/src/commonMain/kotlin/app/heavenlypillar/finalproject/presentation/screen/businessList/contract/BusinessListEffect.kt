package app.heavenlypillar.finalproject.presentation.screen.businessList.contract

sealed interface BusinessListEffect{
    data object GoToCreateBusiness : BusinessListEffect
    data class GoToBusiness(val businessId: String) : BusinessListEffect
    data class ShowError(val error: String) : BusinessListEffect
}