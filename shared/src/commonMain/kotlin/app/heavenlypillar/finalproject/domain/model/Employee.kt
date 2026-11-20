package app.heavenlypillar.finalproject.domain.model

data class Employee(
    val id: String = "",
    val role: EmployeeType = EmployeeType.RegEmployee,
    val name: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val birthdayTimestamp: Long = 0L,
    val startDateTimestamp: Long = 0L,
    val workEmail: String = "",
    val salary: Float = 0f,
    val shift: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val birthday: String = "",
    val startDate: String = "",
    val position: String = "",
    val dayOffLeft: Int = 20,
    val photoUrl: String = "",
    val assignedBusinessIds: List<String> = emptyList(),
    val businessName: String = ""
)

enum class EmployeeType {
    Manager,
    RegEmployee
}