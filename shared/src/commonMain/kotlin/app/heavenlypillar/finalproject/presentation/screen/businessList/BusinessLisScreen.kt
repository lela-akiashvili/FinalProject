package app.heavenlypillar.finalproject.presentation.screen.businessList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.heavenlypillar.finalproject.domain.model.Business
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.presentation.components.AppBottomBar
import app.heavenlypillar.finalproject.presentation.components.AppSearchBar
import app.heavenlypillar.finalproject.presentation.components.BottomTab
import app.heavenlypillar.finalproject.presentation.components.BusinessCard
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.BusinessListEffect
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.BusinessListEvent
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.BusinessListState
import app.heavenlypillar.finalproject.presentation.screen.businessList.contract.SortDir
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import finalproject.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(
    role: EmployeeType,
    onBusinessClick: (String) -> Unit,
    onCreateNewBusiness: () -> Unit,
    onTabSelected: (BottomTab) -> Unit = {},
    viewModel: BusinessListViewModel = koinViewModel(),
    onProfileClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState =
        remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.onEvent(BusinessListEvent.OnRefresh)
        viewModel.effects.collect { effect ->
            when (effect) {
                is BusinessListEffect.GoToCreateBusiness -> onCreateNewBusiness()
                is BusinessListEffect.GoToBusiness -> onBusinessClick(
                    effect.businessId
                )

                is BusinessListEffect.ShowError -> snackbarHostState.showSnackbar(
                    effect.error
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomBar(
                currentRoute = "businesses",
                role = role,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            if (role == EmployeeType.Manager) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(BusinessListEvent.OnCreateNewBusiness) },
                    shape = CircleShape,
                    containerColor = BoldGreen,
                    contentColor = Color.White
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.add_new_icon),
                        contentDescription = "Create Business",
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
                    query = uiState.searchValue,
                    onQueryChange = {
                        viewModel.onEvent(
                            BusinessListEvent.OnSearch(it)
                        )
                    },
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { /* Open filter options */ }
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        val newDir =
                            if (uiState.sortDir == SortDir.Asc) SortDir.Desc else SortDir.Asc
                        viewModel.onEvent(
                            BusinessListEvent.OnSortChange(
                                newDir
                            )
                        )
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
                onRefresh = { viewModel.onEvent(BusinessListEvent.OnRefresh) },
                modifier = Modifier.fillMaxSize()
            ) {
                if (uiState.businessList.isEmpty() && !uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No businesses found.\nTap '+' to add one.",
                            color = SecondaryGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp
                        )
                    }
                } else {
                    if (role == EmployeeType.RegEmployee) {
                        val myBusiness = uiState.businessList.firstOrNull()

                        if (myBusiness != null) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(1),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    BusinessCard(
                                        business = myBusiness,
                                        isBookmarked = myBusiness.id in uiState.bookmarkedIds,
                                        onCardClick = { viewModel.onEvent(BusinessListEvent.OnBusinessClicked(myBusiness.id)) },
                                        onBookmarkToggle = { viewModel.onEvent(BusinessListEvent.OnToggleBookmark(myBusiness.id)) }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(
                                16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(
                                16.dp
                            ),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                uiState.businessList,
                                key = { it.id }) { business ->
                                val isBookmarked =
                                    business.id in uiState.bookmarkedIds
                                BusinessCard(
                                    business = business,
                                    isBookmarked = isBookmarked,
                                    onCardClick = {
                                        viewModel.onEvent(
                                            BusinessListEvent.OnBusinessClicked(
                                                business.id
                                            )
                                        )
                                    },
                                    onBookmarkToggle = {
                                        viewModel.onEvent(
                                            BusinessListEvent.OnToggleBookmark(
                                                business.id
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}