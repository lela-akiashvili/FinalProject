package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.PurchaseOrder
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.PurchaseRepository

class GetPurchasesUseCase(
    private val authRepository: AuthRepository,
    private val purchaseRepository: PurchaseRepository
) {
    suspend operator fun invoke(): Result<List<PurchaseOrder>> {
        val currentEmployee = authRepository.getCurrentEmployee()
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        // Fetching by business ID allows reg employees to see coworker's orders in the same business
        return purchaseRepository.getPurchaseOrdersForBusinesses(currentEmployee.assignedBusinessIds)
    }
}