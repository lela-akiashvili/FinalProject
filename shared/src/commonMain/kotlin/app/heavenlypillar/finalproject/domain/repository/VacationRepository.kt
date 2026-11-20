package app.heavenlypillar.finalproject.domain.repository

import app.heavenlypillar.finalproject.domain.model.Vacation

interface VacationRepository {
    suspend fun getVacationsForEmployee(employeeId: String): Result<List<Vacation>>
    suspend fun requestVacation(vacation: Vacation): Result<Unit>
}