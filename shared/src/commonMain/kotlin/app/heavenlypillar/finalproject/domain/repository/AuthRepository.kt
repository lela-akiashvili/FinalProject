package app.heavenlypillar.finalproject.domain.repository

import app.heavenlypillar.finalproject.domain.model.Employee

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<Employee>
    suspend fun getCurrentEmployee(): Employee?
    suspend fun signOut()
    suspend fun updateAssignedBusinesses(userId: String, newIds: List<String>): Result<Unit>
    suspend fun updateEmployeeProfile(employee: Employee): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
}