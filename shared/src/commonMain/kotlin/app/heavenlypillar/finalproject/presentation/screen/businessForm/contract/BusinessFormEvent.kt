package app.heavenlypillar.finalproject.presentation.screen.businessForm.contract


sealed interface BusinessFormEvent {
    data class LoadBusiness(val id: String?) : BusinessFormEvent
    data object ToggleEditing : BusinessFormEvent
    data class OnNameChanged(val value: String) : BusinessFormEvent
    data class OnAddressChanged(val value: String) : BusinessFormEvent
    data class OnLocationUrlChanged(val value: String) : BusinessFormEvent
    data class OnOpenHoursChanged(val value: String) : BusinessFormEvent
    data class OnEstablishmentDateChanged(val value: String) : BusinessFormEvent
    data class OnManagerNameChanged(val value: String) : BusinessFormEvent
    data class OnEmployeesSummaryChanged(val value: String) : BusinessFormEvent
    data class OnMenuSummaryChanged(val value: String) : BusinessFormEvent
    data class OnServicesSummaryChanged(val value: String) : BusinessFormEvent
    data class OnCoverSelected(val bytes: ByteArray?) : BusinessFormEvent
    data object ToggleManagerDropdown : BusinessFormEvent
    data object OnSaveClicked : BusinessFormEvent
    data object OnDiscardClicked : BusinessFormEvent
}