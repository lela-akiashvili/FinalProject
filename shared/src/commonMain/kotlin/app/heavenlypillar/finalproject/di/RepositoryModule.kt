package app.heavenlypillar.finalproject.di

import app.heavenlypillar.finalproject.data.repository.AuthRepositoryImpl
import app.heavenlypillar.finalproject.data.repository.BusinessRepositoryImpl
import app.heavenlypillar.finalproject.data.repository.EmployeeRepositoryImpl
import app.heavenlypillar.finalproject.data.repository.PurchaseRepositoryImpl
import app.heavenlypillar.finalproject.data.repository.StorageRepositoryImpl
import app.heavenlypillar.finalproject.data.repository.VacationRepositoryImpl
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.domain.repository.BusinessRepository
import app.heavenlypillar.finalproject.domain.repository.EmployeeRepository
import app.heavenlypillar.finalproject.domain.repository.PurchaseRepository
import app.heavenlypillar.finalproject.domain.repository.StorageRepository
import app.heavenlypillar.finalproject.domain.repository.VacationRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl() }
    single<BusinessRepository> { BusinessRepositoryImpl() }
    single<EmployeeRepository> { EmployeeRepositoryImpl() }
    single<VacationRepository> { VacationRepositoryImpl() }
    single<PurchaseRepository> { PurchaseRepositoryImpl() }
    single<StorageRepository> { StorageRepositoryImpl() }
}