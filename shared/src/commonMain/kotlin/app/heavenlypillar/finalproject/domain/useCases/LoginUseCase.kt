package app.heavenlypillar.finalproject.domain.useCases

import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        expectedRole: EmployeeType
    ): Result<Employee> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty."))
        }

        val authResult = authRepository.signIn(email.trim(), password)

        return authResult.fold(
            onSuccess = { employee ->
                // Verify the account's Firestore role matches the login portal
                if (expectedRole == EmployeeType.Manager && employee.role != EmployeeType.Manager) {
                    authRepository.signOut()
                    Result.failure(SecurityException("Access Denied: This account lacks Administrator privileges."))
                } else if (expectedRole == EmployeeType.RegEmployee && employee.role != EmployeeType.RegEmployee) {
                    authRepository.signOut()
                    Result.failure(SecurityException("Access Denied: Administrator accounts must log in via the Administrator portal."))
                } else {
                    Result.success(employee)
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}