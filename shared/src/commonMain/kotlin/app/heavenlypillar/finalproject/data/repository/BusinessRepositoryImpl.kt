package app.heavenlypillar.finalproject.data.repository

import app.heavenlypillar.finalproject.domain.model.Business
import app.heavenlypillar.finalproject.domain.repository.BusinessRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

@Serializable
data class BusinessFirestoreDto(
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
    val photo: String = "",
    val photoUrl: String
)

class BusinessRepositoryImpl : BusinessRepository {
    private val firestore by lazy { Firebase.firestore }

    override suspend fun getAllBusinesses(): Result<List<Business>> = runCatching {
        val snapshot = firestore.collection("businesses").get()
        snapshot.documents.map { doc ->
            val dto = doc.data<BusinessFirestoreDto>()
            dto.toDomain(doc.id)
        }
    }

    override suspend fun getAssignedBusinesses(businessIds: List<String>): Result<List<Business>> = runCatching {
        if (businessIds.isEmpty()) return@runCatching emptyList()
        // Firestore 'in' query supports up to 30 items
        val snapshot = firestore.collection("businesses")
            .where { "id" inArray businessIds.take(30) }
            .get()

        snapshot.documents.map { doc ->
            val dto = doc.data<BusinessFirestoreDto>()
            dto.toDomain(doc.id)
        }
    }

    override suspend fun getBusinessById(businessId: String): Result<Business> = runCatching {
        val doc = firestore.collection("businesses").document(businessId).get()
        doc.data<BusinessFirestoreDto>().toDomain(doc.id)
    }

    override suspend fun createOrUpdateBusiness(business: Business): Result<String> = runCatching {
        val document = if (business.id.isBlank()) {
            firestore.collection("businesses").document
        } else {
            firestore.collection("businesses").document(business.id)
        }
        val generatedId = document.id

        val dto = BusinessFirestoreDto(
            id = generatedId,
            name = business.name,
            hotline = business.hotline,
            establishmentDate = business.establishmentDate,
            openHours = business.openHours,
            menu = business.menu,
            services = business.services,
            managerName = business.managerName,
            locationUrl = business.locationUrl,
            address = business.address,
            photoUrl = business.photoUrl
        )
        document.set(dto)
        generatedId
    }

    private fun BusinessFirestoreDto.toDomain(docId: String): Business {
        return Business(
            id = this.id.ifEmpty { docId },
            name = this.name,
            hotline = this.hotline,
            establishmentDate = this.establishmentDate,
            openHours = this.openHours,
            menu = this.menu,
            services = this.services,
            managerName = this.managerName,
            locationUrl = this.locationUrl,
            address = this.address
        )
    }
}

