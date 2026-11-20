package app.heavenlypillar.finalproject.domain.model

enum class VacationType {
    Vacation,
    SickLeave,
    DayOff,
    PublicHoliday
}

enum class VacationStatus {
    Pending,
    Approved,
    Rejected
}

data class Vacation(
    val id: String = "",
    val employeeId: String = "",
    val employeeName: String = "",
    val type: VacationType = VacationType.DayOff,
    val startDate: String = "",
    val endDate: String = "",
    val daysCount: Int = 1,
    val status: VacationStatus = VacationStatus.Pending,
    val approvedBy: String = ""
)