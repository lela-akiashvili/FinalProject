package app.heavenlypillar.finalproject.data.repository

import app.heavenlypillar.finalproject.domain.model.OrderStatus
import app.heavenlypillar.finalproject.domain.model.PurchaseOrder
import app.heavenlypillar.finalproject.domain.repository.PurchaseRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

@Serializable
data class PurchaseFirestoreDto(
    val id: String = "",
    val subject: String = "",
    val type: String = "",
    val amount: String = "",
    val reason: String = "",
    val approver: String = "",
    val requestedBy: String = "",
    val requesterName: String = "",
    val createdAt: String = "",
    val price: Float = 0f,
    val status: String = "Pending",
    val businessId: String = ""
)

class PurchaseRepositoryImpl : PurchaseRepository {
    private val firestore by lazy { Firebase.firestore }

    override suspend fun createPurchaseOrder(order: PurchaseOrder): Result<Unit> = runCatching {
        val doc = firestore.collection("purchases").document
        val dto = PurchaseFirestoreDto(
            id = doc.id,
            subject = order.subject,
            type = order.type,
            amount = order.amount,
            reason = order.reason,
            approver = order.approver,
            requestedBy = order.requestedBy,
            requesterName = order.requesterName,
            createdAt = order.createdAt,
            price = order.price,
            status = order.status.name,
            businessId = order.businessId
        )
        doc.set(dto)
    }

    override suspend fun getPurchaseOrdersForEmployee(employeeId: String): Result<List<PurchaseOrder>> = runCatching {
        val snapshot = firestore.collection("purchases")
            .where { "requestedBy" equalTo employeeId }
            .get()
        mapSnapshotToDomain(snapshot.documents)
    }

    override suspend fun getPurchaseOrdersForBusinesses(businessIds: List<String>): Result<List<PurchaseOrder>> = runCatching {
        if (businessIds.isEmpty()) return@runCatching emptyList()
        val snapshot = firestore.collection("purchases")
            .where { "businessId".containsAny<String>(businessIds.take(10)) }
            .get()
        mapSnapshotToDomain(snapshot.documents)
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Unit> = runCatching {
        firestore.collection("purchases").document(orderId).update("status" to newStatus.name)
    }

    private fun mapSnapshotToDomain(documents: List<dev.gitlive.firebase.firestore.DocumentSnapshot>): List<PurchaseOrder> {
        return documents.map { doc ->
            val dto = doc.data<PurchaseFirestoreDto>()
            PurchaseOrder(
                id = doc.id,
                subject = dto.subject,
                type = dto.type,
                amount = dto.amount,
                reason = dto.reason,
                approver = dto.approver,
                requestedBy = dto.requestedBy,
                requesterName = dto.requesterName,
                createdAt = dto.createdAt,
                price = dto.price,
                status = OrderStatus.valueOf(dto.status),
                businessId = dto.businessId
            )
        }
    }
}