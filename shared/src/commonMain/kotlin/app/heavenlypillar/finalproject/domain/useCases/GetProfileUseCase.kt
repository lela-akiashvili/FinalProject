package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

data class ProfileData(
    val currentViewer: Employee,
    val targetEmployee: Employee,
    val isOwnProfile: Boolean
)

class GetProfileUseCase(
    private val authRepository: AuthRepository
) {
    private val firestore by lazy { Firebase.firestore }

    suspend operator fun invoke(targetEmployeeId: String?): Result<ProfileData> = runCatching {
        val currentViewer = authRepository.getCurrentEmployee()
            ?: throw IllegalStateException("User not authenticated")

        val isOwn = targetEmployeeId.isNullOrEmpty() || targetEmployeeId == currentViewer.id

        val target = if (isOwn) {
            currentViewer
        } else {
            val doc = firestore.collection("users").document(targetEmployeeId!!).get()
            val dto = doc.data<app.heavenlypillar.finalproject.data.model.EmployeeFirestoreDto>()
            Employee(
                id = doc.id,
                role = if (dto.role == "Manager") app.heavenlypillar.finalproject.domain.model.EmployeeType.Manager else app.heavenlypillar.finalproject.domain.model.EmployeeType.RegEmployee,
                name = dto.name,
                lastName = dto.lastName,
                workEmail = dto.workEmail,
                phoneNumber = dto.phoneNumber,
                address = dto.address,
                birthday = dto.birthday,
                startDate = dto.startDate,
                position = dto.position,
                shift = dto.shift,
                salary = dto.salary,
                dayOffLeft = dto.dayOffLeft,
                photoUrl = dto.photoUrl,
                assignedBusinessIds = dto.assignedBusinessIds,
                businessName = dto.businessName
            )
        }

        ProfileData(
            currentViewer = currentViewer,
            targetEmployee = target,
            isOwnProfile = isOwn
        )
    }
}