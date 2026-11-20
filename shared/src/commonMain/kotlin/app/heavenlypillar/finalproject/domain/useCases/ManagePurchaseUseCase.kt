package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.OrderStatus
import app.heavenlypillar.finalproject.domain.model.PurchaseOrder
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.PurchaseRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ManagePurchaseUseCase(
    private val authRepository: AuthRepository,
    private val purchaseRepository: PurchaseRepository
) {
    suspend fun createOrder(subject: String, type: String, amount: String, reason: String, approver: String): Result<Unit> {
        val currentEmployee = authRepository.getCurrentEmployee()
            ?: return Result.failure(IllegalStateException("Not authenticated"))

        if (subject.isBlank() || amount.isBlank()) return Result.failure(IllegalArgumentException("Subject and Amount are required"))

        val today =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        val order = PurchaseOrder(
            subject = subject,
            type = type,
            amount = amount,
            reason = reason,
            approver = approver,
            requestedBy = currentEmployee.id,
            requesterName = "${currentEmployee.name} ${currentEmployee.lastName}",
            createdAt = today.replace("-", "/"),
            price = amount.toFloatOrNull() ?: 0f,
            status = OrderStatus.Pending,
            businessId = currentEmployee.assignedBusinessIds.firstOrNull() ?: ""
        )
        return purchaseRepository.createPurchaseOrder(order)
    }

    suspend fun updateStatus(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return purchaseRepository.updateOrderStatus(orderId, newStatus)
    }
}