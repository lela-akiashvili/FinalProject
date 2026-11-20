package app.heavenlypillar.finalproject.data.repository

import app.heavenlypillar.finalproject.domain.repository.StorageRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FirebaseStorageResponse(
    val downloadTokens: String = ""
)

class StorageRepositoryImpl : StorageRepository {
    private val bucketName = "androidfinalproject-geo"

    private val httpClient = HttpClient(CIO)
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun uploadImage(bytes: ByteArray, path: String): Result<String> = runCatching {
        val token = Firebase.auth.currentUser?.getIdToken(false)
            ?: throw Exception("User not authenticated for upload")
        val encodedPath = path.removePrefix("/").replace("/", "%2F")

        val url = "https://firebasestorage.googleapis.com/v0/b/$bucketName/o?name=$encodedPath"

        val response = httpClient.post(url) {
            header("Authorization", "Bearer $token")
            header("Content-Type", "image/jpeg")
            setBody(bytes)
        }

        val responseBody: String = response.body()
        val parsedResponse = json.decodeFromString<FirebaseStorageResponse>(responseBody)

        if (parsedResponse.downloadTokens.isEmpty()) {
            throw Exception("Failed to retrieve download token from Firebase")
        }
        "https://firebasestorage.googleapis.com/v0/b/$bucketName/o/$encodedPath?alt=media&token=${parsedResponse.downloadTokens}"
    }
}