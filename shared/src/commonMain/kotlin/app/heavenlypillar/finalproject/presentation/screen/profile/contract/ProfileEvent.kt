package app.heavenlypillar.finalproject.presentation.screen.profile.contract

import app.heavenlypillar.finalproject.domain.model.VacationType

sealed interface ProfileEvent {
    data class LoadProfile(val targetEmployeeId: String?) : ProfileEvent
    data class SelectTab(val tab: ProfileTab) : ProfileEvent
    data object ToggleEditing : ProfileEvent
    data class OnNameChanged(val value: String) : ProfileEvent
    data class OnLastNameChanged(val value: String) : ProfileEvent
    data class OnAddressChanged(val value: String) : ProfileEvent
    data class OnPhoneChanged(val value: String) : ProfileEvent
    data class OnBirthdayChanged(val value: String) : ProfileEvent
    data class OnLanguageChanged(val value: String) : ProfileEvent
    data object SaveProfileClicked : ProfileEvent
    data object DiscardProfileClicked : ProfileEvent
    data object OpenChangePasswordDialog : ProfileEvent
    data object CloseChangePasswordDialog : ProfileEvent
    data class SubmitNewPassword(val newPassword: String) : ProfileEvent
    data object OpenAddVacationDialog : ProfileEvent
    data object CloseAddVacationDialog : ProfileEvent
    data class SubmitVacationRequest(val type: VacationType, val startDate: String, val endDate: String, val days: Int) : ProfileEvent
    data object OpenTerminationDialog : ProfileEvent
    data object CloseTerminationDialog : ProfileEvent
    data object ConfirmTermination : ProfileEvent
    data object SignOutClicked : ProfileEvent
    data class OnAvatarSelected(val bytes: ByteArray?) : ProfileEvent
}

enum class ProfileTab {
    Details,
    VacationDays,
    Termination
}
