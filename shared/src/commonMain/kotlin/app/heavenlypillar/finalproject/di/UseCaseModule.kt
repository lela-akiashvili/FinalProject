package app.heavenlypillar.finalproject.di

import app.heavenlypillar.finalproject.domain.useCases.CreateEmployeeUseCase
import app.heavenlypillar.finalproject.domain.useCases.GetBusinessesUseCase
import app.heavenlypillar.finalproject.domain.useCases.GetEmployeesUseCase
import app.heavenlypillar.finalproject.domain.useCases.GetProfileUseCase
import app.heavenlypillar.finalproject.domain.useCases.GetPurchasesUseCase
import app.heavenlypillar.finalproject.domain.useCases.LoginUseCase
import app.heavenlypillar.finalproject.domain.useCases.ManagePurchaseUseCase
import app.heavenlypillar.finalproject.domain.useCases.RequestVacationUseCase
import app.heavenlypillar.finalproject.domain.useCases.SaveBusinessUseCase
import app.heavenlypillar.finalproject.domain.useCases.TerminateEmployeeUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { LoginUseCase(authRepository = get()) }
    factory { GetBusinessesUseCase(authRepository = get(), businessRepository = get()) }
    factory { SaveBusinessUseCase(businessRepository = get(), authRepository = get()) }
    factory { GetEmployeesUseCase(authRepository = get(), employeeRepository = get()) }
    factory { CreateEmployeeUseCase(authRepository = get(), employeeRepository = get()) }
    factory { GetProfileUseCase(authRepository = get()) }
    factory { RequestVacationUseCase(authRepository = get(), vacationRepository = get()) }
    factory { TerminateEmployeeUseCase(authRepository = get(), employeeRepository = get()) }
    factory { GetPurchasesUseCase(authRepository = get(), purchaseRepository = get()) }
    factory { ManagePurchaseUseCase(authRepository = get(), purchaseRepository = get()) }
}