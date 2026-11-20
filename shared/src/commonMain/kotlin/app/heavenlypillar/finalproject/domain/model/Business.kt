package app.heavenlypillar.finalproject.domain.model

data class Business(
    val id: String = "",
    val name: String = "",
    val hotline: String = "",
    val establishmentDate: Long = 0L,
    val openHours: String = "",
    val menu: List<String> = emptyList(),
    val services: List<String> = emptyList(),
    val managerName: String = "",
    val locationUrl: String = "",
    val address: String = "",
    val photoUrl: String = ""
)

enum class MenuItemType {
    Beverage,
    HotFood,
    FastFood,
    Snack
}