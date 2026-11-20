package app.heavenlypillar.finalproject.presentation.screen.businessForm.contract


data class BusinessFormState(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val locationUrl: String = "",
    val openHours: String = "",
    val establishmentDate: String = "",
    val managerName: String = "",
    val employeesSummary: String = "",
    val menuSummary: String = "",
    val servicesSummary: String = "",
    val coverBytes: ByteArray? = null,
    val photoUrl: String = "",
    val availableManagers: List<String> = emptyList(),
    val isManagerDropdownExpanded: Boolean = false,
    val isEditing: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)