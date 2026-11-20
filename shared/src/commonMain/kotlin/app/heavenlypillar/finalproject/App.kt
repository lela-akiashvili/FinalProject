package app.heavenlypillar.finalproject

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.heavenlypillar.finalproject.di.*
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.repository.AuthRepository
import app.heavenlypillar.finalproject.presentation.components.BottomTab
import app.heavenlypillar.finalproject.presentation.screen.businessForm.BusinessFormScreen
import app.heavenlypillar.finalproject.presentation.screen.businessList.BusinessListScreen
import app.heavenlypillar.finalproject.presentation.screen.createEmployee.CreateEmployeeScreen
import app.heavenlypillar.finalproject.presentation.screen.employeesList.EmployeesListScreen
import app.heavenlypillar.finalproject.presentation.screen.login.LoginScreen
import app.heavenlypillar.finalproject.presentation.screen.profile.ProfileScreen
import app.heavenlypillar.finalproject.presentation.screen.purchases.PurchasesListScreen
import app.heavenlypillar.finalproject.presentation.screen.roleAssertion.RoleAssertionScreen
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(firebaseModule, repositoryModule, useCaseModule, viewModelModule)
        }
    ) {
        val authRepository: AuthRepository = koinInject()
        val navController = rememberNavController()

        LaunchedEffect(Unit) {
            val employee = authRepository.getCurrentEmployee()
            if (employee != null) {
                navController.navigate("businessList/${employee.role.name}") {
                    popUpTo(0)
                }
            } else {
                navController.navigate("roleAssertion") { popUpTo(0) }
            }
        }

        MaterialTheme {
            // statusBarsPadding() pushes content down so it doesn't hide behind the clock/battery
            Surface(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                NavHost(
                    navController = navController,
                    startDestination = "loading"
                ) {
                    composable("loading") { /* Empty loading screen */ }

                    composable("roleAssertion") {
                        RoleAssertionScreen(
                            viewModel = org.koin.compose.viewmodel.koinViewModel(),
                            onNavigateToLogin = { role ->
                                navController.navigate("login/${role.name}")
                            }
                        )
                    }

                    composable("createEmployee") {
                        CreateEmployeeScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable("login/{roleName}") { backStackEntry ->
                        val roleStr = backStackEntry.arguments?.getString("roleName") ?: "RegEmployee"
                        val role = EmployeeType.valueOf(roleStr)
                        LoginScreen(
                            targetRole = role,
                            viewModel = org.koin.compose.viewmodel.koinViewModel(),
                            onNavigateToBusinesses = { authRole ->
                                navController.navigate("businessList/${authRole.name}") {
                                    popUpTo("login/{roleName}") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("createBusiness") {
                        BusinessFormScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("profile") {
                        ProfileScreen(
                            targetEmployeeId = null,
                            onNavigateBack = { navController.popBackStack() },
                            onSignedOut = {
                                navController.navigate("roleAssertion") { popUpTo(0) }
                            }
                        )
                    }

                    composable("profile/{employeeId}") { backStackEntry ->
                        val empId = backStackEntry.arguments?.getString("employeeId")
                        ProfileScreen(
                            targetEmployeeId = empId,
                            onNavigateBack = { navController.popBackStack() },
                            onSignedOut = {
                                navController.navigate("roleAssertion") { popUpTo(0) }
                            }
                        )
                    }

                    composable("staff/{roleName}") { backStackEntry ->
                        val roleStr = backStackEntry.arguments?.getString("roleName") ?: "RegEmployee"
                        val role = EmployeeType.valueOf(roleStr)

                        EmployeesListScreen(
                            role = role,
                            onNavigateToCreateEmployee = { navController.navigate("createEmployee") },
                            onNavigateToEmployeeDetails = { employeeId ->
                                navController.navigate("profile/$employeeId")
                            },
                            onProfileClick = { navController.navigate("profile") },
                            onTabSelected = { tab ->
                                when (tab) {
                                    BottomTab.Businesses -> navController.navigate("businessList/${role.name}")
                                    BottomTab.Purchases -> navController.navigate("purchases/${role.name}")
                                    BottomTab.Staff -> Unit
                                    else -> Unit
                                }
                            }
                        )
                    }

                    composable("businessList/{roleName}") { backStackEntry ->
                        val roleStr = backStackEntry.arguments?.getString("roleName") ?: "RegEmployee"
                        val role = EmployeeType.valueOf(roleStr)

                        BusinessListScreen(
                            role = role,
                            onBusinessClick = { id ->
                                navController.navigate("businessDetails/$id")
                            },
                            onCreateNewBusiness = { navController.navigate("createBusiness") },
                            onProfileClick = { navController.navigate("profile") },
                            onTabSelected = { tab ->
                                when (tab) {
                                    BottomTab.Staff -> navController.navigate("staff/${role.name}")
                                    BottomTab.Purchases -> navController.navigate("purchases/${role.name}")
                                    else -> Unit
                                }
                            }
                        )
                    }

                    composable("businessDetails/{businessId}") { backStackEntry ->
                        val businessId = backStackEntry.arguments?.getString("businessId")
                        BusinessFormScreen(
                            businessId = businessId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("purchases/{roleName}") { backStackEntry ->
                        val roleStr = backStackEntry.arguments?.getString("roleName") ?: "RegEmployee"
                        val role = EmployeeType.valueOf(roleStr)

                        PurchasesListScreen(
                            onTabSelected = { tab ->
                                when (tab) {
                                    BottomTab.Businesses -> navController.navigate("businessList/${role.name}")
                                    BottomTab.Staff -> navController.navigate("staff/${role.name}")
                                    BottomTab.Purchases -> Unit
                                    else -> Unit
                                }
                            },
                            onProfileClick = { navController.navigate("profile") }
                        )
                    }
                }
            }
        }
    }
}