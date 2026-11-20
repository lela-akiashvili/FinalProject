package app.heavenlypillar.finalproject.data.repository

import app.heavenlypillar.finalproject.data.model.EmployeeFirestoreDto
import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.serialization.Serializable


class AuthRepositoryImpl : AuthRepository {
    private val auth by lazy { Firebase.auth }
    private val firestore by lazy { Firebase.firestore }

    override suspend fun signIn(email: String, password: String): Result<Employee> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password)
            val uid = authResult.user?.uid ?: throw IllegalStateException("User ID not found")

            val doc = firestore.collection("users").document(uid).get()
            val dto = doc.data<EmployeeFirestoreDto>()

            val employee = Employee(
                id = uid,
                role = if (dto.role == "Manager") EmployeeType.Manager else EmployeeType.RegEmployee,
                name = dto.name,
                lastName = dto.lastName,
                age = dto.age,
                workEmail = dto.workEmail,
                salary = dto.salary,
                shift = dto.shift,
                assignedBusinessIds = dto.assignedBusinessIds
            )
            Result.success(employee)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentEmployee(): Employee? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = firestore.collection("users").document(uid).get()
            val dto = doc.data<EmployeeFirestoreDto>()
            Employee(
                id = uid,
                role = if (dto.role == "Manager") EmployeeType.Manager else EmployeeType.RegEmployee,
                name = dto.name,
                lastName = dto.lastName,
                assignedBusinessIds = dto.assignedBusinessIds
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateAssignedBusinesses(userId: String, newIds: List<String>): Result<Unit> = runCatching {
        firestore.collection("users").document(userId).update("assignedBusinessIds" to newIds)
    }

    override suspend fun updateEmployeeProfile(employee: Employee): Result<Unit> = runCatching {
        val dto = EmployeeFirestoreDto(
            id = employee.id,
            role = employee.role.name,
            name = employee.name,
            lastName = employee.lastName,
            workEmail = employee.workEmail,
            phoneNumber = employee.phoneNumber,
            address = employee.address,
            birthday = employee.birthday,
            startDate = employee.startDate,
            position = employee.position,
            shift = employee.shift,
            salary = employee.salary,
            dayOffLeft = employee.dayOffLeft,
            photoUrl = employee.photoUrl,
            assignedBusinessIds = employee.assignedBusinessIds,
            businessName = employee.businessName
        )
        firestore.collection("users").document(employee.id).set(dto)
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> = runCatching {
        auth.currentUser?.updatePassword(newPassword)
            ?: throw IllegalStateException("No active authenticated session")
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}