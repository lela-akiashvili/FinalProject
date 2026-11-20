package app.heavenlypillar.finalproject.presentation.screen.profile.contract

import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.model.Vacation

data class ProfileState(
    val isOwnProfile: Boolean = true,
    val selectedTab: ProfileTab = ProfileTab.Details,
    val isEditing: Boolean = false,
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val address: String = "",
    val startDate: String = "",
    val phoneNumber: String = "",
    val birthday: String = "",
    val preferredLanguage: String = "English",
    val vacations: List<Vacation> = emptyList(),
    val ptoDaysLeft: Int = 20,
    val isAddVacationDialogOpen: Boolean = false,
    val isChangePasswordDialogOpen: Boolean = false,
    val isTerminationConfirmDialogOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val viewerRole: EmployeeType = EmployeeType.RegEmployee,
    val avatarBytes: ByteArray? = null
)



