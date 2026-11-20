package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.EmployeeRepository

class TerminateEmployeeUseCase(
    private val authRepository: AuthRepository,
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(targetEmployeeId: String): Result<Unit> {
        val currentViewer = authRepository.getCurrentEmployee()
            ?: return Result.failure(IllegalStateException("Not logged in"))

        if (currentViewer.role != EmployeeType.Manager) {
            return Result.failure(IllegalStateException("Unauthorized: Only Managers can terminate employees."))
        }

        return employeeRepository.deleteEmployee(targetEmployeeId)
    }
}