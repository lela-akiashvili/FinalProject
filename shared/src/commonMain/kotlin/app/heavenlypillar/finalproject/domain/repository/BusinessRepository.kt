package app.heavenlypillar.finalproject.domain.repository

import app.heavenlypillar.finalproject.domain.model.Business

interface BusinessRepository {
    suspend fun getAssignedBusinesses(businessIds: List<String>): Result<List<Business>>
    suspend fun getAllBusinesses(): Result<List<Business>>
    suspend fun getBusinessById(businessId: String): Result<Business>
    suspend fun createOrUpdateBusiness(business: Business): Result<String>
}