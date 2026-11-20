package app.heavenlypillar.finalproject.domain.repository

import app.heavenlypillar.finalproject.domain.model.OrderStatus
import app.heavenlypillar.finalproject.domain.model.PurchaseOrder

interface PurchaseRepository {
    suspend fun createPurchaseOrder(order: PurchaseOrder): Result<Unit>
    suspend fun getPurchaseOrdersForEmployee(employeeId: String): Result<List<PurchaseOrder>>
    suspend fun getPurchaseOrdersForBusinesses(businessIds: List<String>): Result<List<PurchaseOrder>>
    suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit>
}