package app.heavenlypillar.finalproject.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeFirestoreDto(
    val id: String = "",
    val role: String = "RegEmployee",
    val name: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val workEmail: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val birthday: String = "",
    val startDate: String = "",
    val position: String = "",
    val shift: String = "",
    val salary: Float = 0f,
    val dayOffLeft: Int = 20,
    val photoUrl: String = "",
    val assignedBusinessIds: List<String> = emptyList(),
    val businessName: String = "",
    val temporaryPassword: String = ""
)