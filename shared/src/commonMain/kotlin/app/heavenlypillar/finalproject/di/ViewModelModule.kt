package app.heavenlypillar.finalproject.di

import app.heavenlypillar.finalproject.presentation.screen.businessForm.BusinessFormViewModel
import app.heavenlypillar.finalproject.presentation.screen.businessList.BusinessListViewModel
import app.heavenlypillar.finalproject.presentation.screen.createEmployee.CreateEmployeeViewModel
import app.heavenlypillar.finalproject.presentation.screen.employeesList.EmployeesListViewModel
import app.heavenlypillar.finalproject.presentation.screen.login.LoginViewModel
import app.heavenlypillar.finalproject.presentation.screen.profile.ProfileViewModel
import app.heavenlypillar.finalproject.presentation.screen.purchases.PurchasesViewModel
import app.heavenlypillar.finalproject.presentation.screen.roleAssertion.RoleAssertionViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { RoleAssertionViewModel() }
    viewModel { LoginViewModel(loginUseCase = get()) }
    viewModel { BusinessListViewModel(getBusinessesUseCase = get()) }
    viewModel { BusinessFormViewModel(saveBusinessUseCase = get(), getBusinessesUseCase = get(), storageRepository = get()) }
    viewModel { EmployeesListViewModel(getEmployeesUseCase = get()) }
    viewModel { CreateEmployeeViewModel(get(), get(), storageRepository = get()) }
    viewModel {
        ProfileViewModel(
            getProfileUseCase = get(),
            requestVacationUseCase = get(),
            terminateEmployeeUseCase = get(),
            authRepository = get(),
            vacationRepository = get(),
            storageRepository = get()
        )
    }
    viewModel { PurchasesViewModel(get(), get(), get(), employeeRepository = get()) }
}