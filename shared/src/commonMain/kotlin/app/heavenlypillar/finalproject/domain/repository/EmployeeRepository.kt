package app.heavenlypillar.finalproject.domain.repository

import app.heavenlypillar.finalproject.domain.model.Employee

interface EmployeeRepository {
    suspend fun getEmployeesByBusiness(businessIds: List<String>): Result<List<Employee>>
    suspend fun createEmployee(employee: Employee): Result<String>
    suspend fun deleteEmployee(employeeId: String): Result<Unit>
}