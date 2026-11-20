package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.Business
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.BusinessRepository

class GetBusinessesUseCase(
    private val authRepository: AuthRepository,
    private val businessRepository: BusinessRepository
) {
    suspend operator fun invoke(): Result<List<Business>> {
        val currentEmployee = authRepository.getCurrentEmployee()
            ?: return Result.failure(IllegalStateException("No authenticated session"))

        return if (currentEmployee.role == EmployeeType.Manager) {
            // Managers view all businesses in their system
            val all = businessRepository.getAllBusinesses().getOrNull().orEmpty()
            if (all.isNotEmpty()) {
                Result.success(all)
            } else {
                businessRepository.getAssignedBusinesses(currentEmployee.assignedBusinessIds)
            }
        } else {
            // Regular employees only view their single assigned workplace
            businessRepository.getAssignedBusinesses(currentEmployee.assignedBusinessIds.take(1))
        }
    }
}