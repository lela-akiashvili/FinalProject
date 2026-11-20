package app.heavenlypillar.finalproject.presentation.screen.employeesList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.presentation.components.AppBottomBar
import app.heavenlypillar.finalproject.presentation.components.AppSearchBar
import app.heavenlypillar.finalproject.presentation.components.BottomTab
import app.heavenlypillar.finalproject.presentation.components.EmployeeCard
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.SortDir
import app.heavenlypillar.finalproject.presentation.screen.employeesList.contract.EmployeesListEffect
import app.heavenlypillar.finalproject.presentation.screen.employeesList.contract.EmployeesListEvent
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import finalproject.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesListScreen(
    role: EmployeeType,
    onNavigateToCreateEmployee: () -> Unit,
    onNavigateToEmployeeDetails: (String) -> Unit,
    onTabSelected: (BottomTab) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: EmployeesListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(EmployeesListEvent.OnRefresh)
        viewModel.effects.collect { effect ->
            when (effect) {
                EmployeesListEffect.NavigateToCreateEmployee -> onNavigateToCreateEmployee()
                is EmployeesListEffect.NavigateToEmployeeDetails -> onNavigateToEmployeeDetails(effect.employeeId)
                is EmployeesListEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomBar(
                currentRoute = "staff",
                role = role,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (role == EmployeeType.Manager) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(EmployeesListEvent.OnCreateEmployeeClicked) },
                    shape = CircleShape,
                    containerColor = BoldGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.add_new_icon),
                        contentDescription = "Create Employee",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.onEvent(EmployeesListEvent.OnSearch(it)) },
                    placeholder = stringResource(Res.string.search),
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(Res.drawable.profile_icon),
                    contentDescription = "Profile",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (role == EmployeeType.Manager) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { /* Open Filter Dialog */ }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.filter_icon),
                            contentDescription = "Filter",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.filter),
                            color = BoldGreen,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val next = if (uiState.sortDir == SortDir.Asc) SortDir.Desc else SortDir.Asc
                        viewModel.onEvent(EmployeesListEvent.OnSortChange(next))
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.sort_icon),
                        contentDescription = "Sort",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(Res.string.sort),
                        color = BoldGreen,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.onEvent(EmployeesListEvent.OnRefresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                val filtered = uiState.employees.filter {
                    it.name.contains(uiState.searchQuery, ignoreCase = true) ||
                            it.lastName.contains(uiState.searchQuery, ignoreCase = true) ||
                            it.position.contains(uiState.searchQuery, ignoreCase = true)
                }

                if (filtered.isEmpty() && !uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No employees found.",
                            color = SecondaryGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered, key = { it.id }) { employee ->
                            EmployeeCard(
                                employee = employee,
                                viewerRole = role,
                                onCardClick = { viewModel.onEvent(EmployeesListEvent.OnEmployeeClicked(employee.id)) },
                                onEditClick = { viewModel.onEvent(EmployeesListEvent.OnEmployeeClicked(employee.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}