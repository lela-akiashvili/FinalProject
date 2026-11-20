package app.heavenlypillar.finalproject.data.repository

import app.heavenlypillar.finalproject.domain.model.Vacation
import app.heavenlypillar.finalproject.domain.model.VacationStatus
import app.heavenlypillar.finalproject.domain.model.VacationType
import app.heavenlypillar.finalproject.domain.repository.VacationRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable

@Serializable
data class VacationFirestoreDto(
    val id: String = "",
    val employeeId: String = "",
    val employeeName: String = "",
    val type: String = "DayOff",
    val startDate: String = "",
    val endDate: String = "",
    val daysCount: Int = 1,
    val status: String = "Pending",
    val approvedBy: String = ""
)

class VacationRepositoryImpl : VacationRepository {
    private val firestore by lazy { Firebase.firestore }

    override suspend fun getVacationsForEmployee(employeeId: String): Result<List<Vacation>> = runCatching {
        val snapshot = firestore.collection("vacations")
            .where { "employeeId" equalTo employeeId }
            .get()

        snapshot.documents.map { doc ->
            val dto = doc.data<VacationFirestoreDto>()
            Vacation(
                id = doc.id,
                employeeId = dto.employeeId,
                employeeName = dto.employeeName,
                type = VacationType.valueOf(dto.type),
                startDate = dto.startDate,
                endDate = dto.endDate,
                daysCount = dto.daysCount,
                status = VacationStatus.valueOf(dto.status),
                approvedBy = dto.approvedBy
            )
        }
    }

    override suspend fun requestVacation(vacation: Vacation): Result<Unit> = runCatching {
        val doc = firestore.collection("vacations").document
        val dto = VacationFirestoreDto(
            id = doc.id,
            employeeId = vacation.employeeId,
            employeeName = vacation.employeeName,
            type = vacation.type.name,
            startDate = vacation.startDate,
            endDate = vacation.endDate,
            daysCount = vacation.daysCount,
            status = vacation.status.name,
            approvedBy = vacation.approvedBy
        )
        doc.set(dto)
    }
}