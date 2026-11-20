package app.heavenlypillar.finalproject.domain.model

enum class OrderStatus {
    Pending,
    Approved,
    Rejected,
    Completed
}

data class PurchaseOrder(
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
    val status: OrderStatus = OrderStatus.Pending,
    val businessId: String = ""
)