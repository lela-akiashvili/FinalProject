package app.heavenlypillar.finalproject.data.repository

import app.heavenlypillar.finalproject.data.model.EmployeeFirestoreDto
import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.EmployeeRepository
import app.heavenlypillar.finalproject.utils.Secrets
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
private data class SignUpRestRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

@Serializable
private data class SignUpRestResponse(
    val localId: String,
    val email: String,
    val idToken: String? = null
)

class EmployeeRepositoryImpl : EmployeeRepository {
    private val firestore by lazy { Firebase.firestore }

    private val firebaseWebApiKey = Secrets.FIREBASE_WEB_API_KEY
    private val httpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    override suspend fun createEmployee(employee: Employee): Result<String> = runCatching {
        val temporaryPassword = "Temp@" + (100000..999999).random() + "Aa!"

        val response = httpClient.post("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$firebaseWebApiKey") {
            contentType(ContentType.Application.Json)
            setBody(SignUpRestRequest(email = employee.workEmail, password = temporaryPassword))
        }

        if (!response.status.isSuccess()) {
            val errorBody = response.body<String>()
            if (errorBody.contains("EMAIL_EXISTS")) {
                throw IllegalStateException("An account with email ${employee.workEmail} already exists.")
            }
            throw IllegalStateException("Failed to register employee in Firebase Auth: $errorBody")
        }

        val authResult = response.body<SignUpRestResponse>()
        val newAuthUid = authResult.localId

        val dto = EmployeeFirestoreDto(
            id = newAuthUid,
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
            dayOffLeft = 20,
            photoUrl = employee.photoUrl,
            assignedBusinessIds = employee.assignedBusinessIds,
            businessName = employee.businessName
        )

        firestore.collection("users").document(newAuthUid).set(dto)
        newAuthUid
    }

    override suspend fun getEmployeesByBusiness(businessIds: List<String>): Result<List<Employee>> = runCatching {
        if (businessIds.isEmpty()) return@runCatching emptyList()

        val snapshot = firestore.collection("users")
            .where { "assignedBusinessIds".containsAny<String>(businessIds.take(10)) }
            .get()

        snapshot.documents.map { doc ->
            val dto = doc.data<EmployeeFirestoreDto>()
            Employee(
                id = doc.id,
                role = if (dto.role == "Manager") EmployeeType.Manager else EmployeeType.RegEmployee,
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
    }

    override suspend fun deleteEmployee(employeeId: String): Result<Unit> = runCatching {
        firestore.collection("users").document(employeeId).delete()
    }
}