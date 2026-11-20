package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.EmployeeRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth

class CreateEmployeeUseCase(
    private val authRepository: AuthRepository,
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(employee: Employee): Result<String> {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()

        if (employee.name.isBlank() || employee.lastName.isBlank()) {
            return Result.failure(IllegalArgumentException("Name and Surname are required."))
        }
        if (!employee.workEmail.matches(emailRegex)) {
            return Result.failure(IllegalArgumentException("Valid email format required."))
        }
        if (employee.position.isBlank()) {
            return Result.failure(IllegalArgumentException("Position is required."))
        }

        val currentManager = authRepository.getCurrentEmployee()
        val employeeToCreate = employee.copy(
            assignedBusinessIds = currentManager?.assignedBusinessIds.orEmpty(),
            businessName = currentManager?.businessName.orEmpty()
        )

        val createResult = employeeRepository.createEmployee(employeeToCreate)

        createResult.onSuccess {
            runCatching {
                Firebase.auth.sendPasswordResetEmail(employee.workEmail)
            }
        }

        return createResult
    }
}