package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.Business
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.BusinessRepository

class SaveBusinessUseCase(
    private val businessRepository: BusinessRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(business: Business): Result<String> {
        if (business.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Business Name cannot be empty."))
        }

        return businessRepository.createOrUpdateBusiness(business).onSuccess { newBusinessId ->
            val employee = authRepository.getCurrentEmployee()
            if (employee != null && employee.role == EmployeeType.Manager) {
                val updatedIds = (employee.assignedBusinessIds + newBusinessId).distinct()
                authRepository.updateAssignedBusinesses(employee.id, updatedIds)
            }
        }
    }
}