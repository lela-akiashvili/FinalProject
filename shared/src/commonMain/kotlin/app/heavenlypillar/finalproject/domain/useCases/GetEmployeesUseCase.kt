package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.EmployeeRepository

class GetEmployeesUseCase(
    private val authRepository: AuthRepository,
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(): Result<List<Employee>> {
        val currentEmployee = authRepository.getCurrentEmployee()
            ?: return Result.failure(IllegalStateException("No active user session"))

        return employeeRepository.getEmployeesByBusiness(currentEmployee.assignedBusinessIds)
    }
}