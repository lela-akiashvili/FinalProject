package app.heavenlypillar.finalproject.data.model
import kotlinx.serialization.Serializable

@Serializable
data class BusinessesListDto(
    val collection: List<BusinessesDto>
            ) {
    @Serializable
    data class BusinessesDto (
        val id: String =
            "",
        val name: String =
            "",
        val hotline: String =
            "",
        val establishmentDate: Long =
            0L,
        val openHours: String =
            "",
        val employees: List<EmployeeDto> =
            emptyList(),
        val menu: List<String> =
            emptyList(),
        val services: List<String> =
            emptyList(),
        val managerName: String =
            "",
        val locationUrl: String =
            "",
        val address: String =
            "",
        val inventory: List<InventoryItemDto> =
            emptyList()

    )

}
@Serializable
data class EmployeeDto(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val email: String = ""
)

@Serializable
data class InventoryItemDto(
    val id: String = "",
    val name: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)