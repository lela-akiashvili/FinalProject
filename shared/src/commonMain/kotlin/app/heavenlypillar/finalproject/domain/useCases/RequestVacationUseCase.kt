package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.*
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.VacationRepository

class RequestVacationUseCase(
    private val authRepository: AuthRepository,
    private val vacationRepository: VacationRepository
) {
    suspend operator fun invoke(
        type: VacationType,
        startDate: String,
        endDate: String,
        daysCount: Int
    ): Result<Unit> {
        val currentEmployee = authRepository.getCurrentEmployee()
            ?: return Result.failure(IllegalStateException("No active session"))

        if (currentEmployee.dayOffLeft < daysCount) {
            return Result.failure(IllegalArgumentException("Not enough PTO days left ($daysCount requested, ${currentEmployee.dayOffLeft} available)"))
        }

        val isManager = currentEmployee.role == EmployeeType.Manager
        val vacation = Vacation(
            employeeId = currentEmployee.id,
            employeeName = "${currentEmployee.name} ${currentEmployee.lastName}",
            type = type,
            startDate = startDate,
            endDate = endDate,
            daysCount = daysCount,
            status = if (isManager) VacationStatus.Approved else VacationStatus.Pending,
            approvedBy = if (isManager) "${currentEmployee.name} ${currentEmployee.lastName}" else ""
        )

        return vacationRepository.requestVacation(vacation).onSuccess {
            // Deduct PTO balance if auto-approved
            if (isManager) {
                val updatedEmployee = currentEmployee.copy(dayOffLeft = currentEmployee.dayOffLeft - daysCount)
                authRepository.updateEmployeeProfile(updatedEmployee)
            }
        }
    }
}