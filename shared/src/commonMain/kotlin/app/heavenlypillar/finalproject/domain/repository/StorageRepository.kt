package app.heavenlypillar.finalproject.domain.repository

interface StorageRepository {
    suspend fun uploadImage(bytes: ByteArray, path: String): Result<String>
}