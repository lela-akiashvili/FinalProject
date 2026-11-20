package app.heavenlypillar.finalproject.presentation.screen.purchases

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import app.heavenlypillar.finalproject.domain.model.OrderStatus
import app.heavenlypillar.finalproject.domain.model.PurchaseOrder
import app.heavenlypillar.finalproject.presentation.components.AppBottomBar
import app.heavenlypillar.finalproject.presentation.components.AppButton
import app.heavenlypillar.finalproject.presentation.components.AppDetailsBox
import app.heavenlypillar.finalproject.presentation.components.AppSearchBar
import app.heavenlypillar.finalproject.presentation.components.BottomTab
import app.heavenlypillar.finalproject.presentation.components.AppTextField
import app.heavenlypillar.finalproject.presentation.screen.purchases.contract.*
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import finalproject.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PurchasesListScreen(
    onTabSelected: (BottomTab) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: PurchasesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is PurchasesListEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomBar(
                currentRoute = "purchases",
                role = state.role,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        if (state.isInitialized)  {
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
                        query = state.searchQuery,
                        onQueryChange = {
                            viewModel.onEvent(
                                PurchasesListEvent.OnSearch(it)
                            )
                        },
                        placeholder = "Search",
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

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (state.role == EmployeeType.Manager) {
                        PurchaseTabItem(
                            "Pending Orders",
                            state.selectedTab == PurchaseTab.PendingOrders,
                            Color(0xFFD98327)
                        ) {
                            viewModel.onEvent(
                                PurchasesListEvent.SelectTab(
                                    PurchaseTab.PendingOrders
                                )
                            )
                        }
                        PurchaseTabItem(
                            "Approved",
                            state.selectedTab == PurchaseTab.Approved,
                            Color(0xFF9B5E1A)
                        ) {
                            viewModel.onEvent(
                                PurchasesListEvent.SelectTab(
                                    PurchaseTab.Approved
                                )
                            )
                        }
                        PurchaseTabItem(
                            "Past Orders",
                            state.selectedTab == PurchaseTab.PastOrders,
                            Color(0xFF6B8E6B)
                        ) {
                            viewModel.onEvent(
                                PurchasesListEvent.SelectTab(
                                    PurchaseTab.PastOrders
                                )
                            )
                        }
                    } else {
                        PurchaseTabItem(
                            "Create New Order",
                            state.selectedTab == PurchaseTab.CreateNewOrder,
                            Color(0xFFD98327)
                        ) {
                            viewModel.onEvent(
                                PurchasesListEvent.SelectTab(
                                    PurchaseTab.CreateNewOrder
                                )
                            )
                        }
                        PurchaseTabItem(
                            "Past Orders",
                            state.selectedTab == PurchaseTab.PastOrders,
                            Color(0xFF6B8E6B)
                        ) {
                            viewModel.onEvent(
                                PurchasesListEvent.SelectTab(
                                    PurchaseTab.PastOrders
                                )
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    when (state.selectedTab) {
                        PurchaseTab.CreateNewOrder -> CreateOrderForm(
                            state,
                            viewModel
                        )

                        else -> PurchaseOrderList(state, viewModel)
                    }
                }
            }
        }else { Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) } }
    }
}

@Composable
private fun PurchaseTabItem(title: String, isSelected: Boolean, activeColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
            .background(if (isSelected) activeColor else activeColor.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun CreateOrderForm(state: PurchasesListState, viewModel: PurchasesViewModel) {
    AppDetailsBox(
        onEditClick = {},
        isEditing = false
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            Icon(painterResource(Res.drawable.purchases_icon), null, tint = BoldGreen, modifier = Modifier.size(24.dp))
        }

        PurchaseInputRow("Subject", state.formSubject) { viewModel.onEvent(PurchasesListEvent.OnSubjectChanged(it)) }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Type:", fontSize = 11.sp, color = SecondaryGray, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(2.dp))
            Box {
                AppTextField(value = state.formType.ifEmpty { "Select Type" }, onValueChange = {}, placeholder = "", readOnly = true)
                Box(modifier = Modifier.matchParentSize().clickable { viewModel.onEvent(PurchasesListEvent.ToggleTypeDropdown) })
                DropdownMenu(expanded = state.isTypeDropdownExpanded, onDismissRequest = { viewModel.onEvent(PurchasesListEvent.ToggleTypeDropdown) }) {
                    state.availableTypes.forEach { type ->
                        DropdownMenuItem(text = { Text(type, fontFamily = FontFamily.Monospace) }, onClick = { viewModel.onEvent(PurchasesListEvent.OnTypeChanged(type)) })
                    }
                }
            }
        }

        PurchaseInputRow("Amount:", state.formAmount) { viewModel.onEvent(PurchasesListEvent.OnAmountChanged(it)) }
        PurchaseInputRow("Reason:", state.formReason) { viewModel.onEvent(PurchasesListEvent.OnReasonChanged(it)) }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            Text("Approver:", fontSize = 11.sp, color = SecondaryGray, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(2.dp))
            Box {
                AppTextField(value = state.formApprover.ifEmpty { "Select Manager" }, onValueChange = {}, placeholder = "", readOnly = true)
                Box(modifier = Modifier.matchParentSize().clickable { viewModel.onEvent(PurchasesListEvent.ToggleApproverDropdown) })
                DropdownMenu(expanded = state.isApproverDropdownExpanded, onDismissRequest = { viewModel.onEvent(PurchasesListEvent.ToggleApproverDropdown) }) {
                    if (state.availableApprovers.isEmpty()) {
                        DropdownMenuItem(text = { Text("No managers found", fontFamily = FontFamily.Monospace) }, onClick = { })
                    } else {
                        state.availableApprovers.forEach { manager ->
                            DropdownMenuItem(text = { Text(manager, fontFamily = FontFamily.Monospace) }, onClick = { viewModel.onEvent(PurchasesListEvent.OnApproverChanged(manager)) })
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppButton("Create", { viewModel.onEvent(PurchasesListEvent.SubmitOrder) }, Modifier.weight(1f))
            OutlinedButton(
                onClick = { viewModel.onEvent(PurchasesListEvent.DiscardOrder) },
                modifier = Modifier.weight(1f).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, BoldGreen)
            ) {
                Text("Discard", color = BoldGreen, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
@Composable
private fun PurchaseInputRow(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontSize = 11.sp, color = SecondaryGray, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(2.dp))
        AppTextField(value = value, onValueChange = onValueChange, placeholder = "")
    }
}

@Composable
private fun PurchaseOrderList(state: PurchasesListState, viewModel: PurchasesViewModel) {
    val filteredOrders = state.orders.filter {
        val matchesTab = when (state.selectedTab) {
            PurchaseTab.PendingOrders -> it.status == OrderStatus.Pending
            PurchaseTab.Approved -> it.status == OrderStatus.Approved
            PurchaseTab.PastOrders -> it.status == OrderStatus.Completed || it.status == OrderStatus.Rejected || (state.role == EmployeeType.RegEmployee && it.status != OrderStatus.Pending)
            else -> true
        }
        val matchesSearch = it.subject.contains(state.searchQuery, ignoreCase = true)
        matchesTab && matchesSearch
    }

    AppDetailsBox(onEditClick = {}, isEditing = false) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painterResource(Res.drawable.filter_icon), null, tint = Color.Unspecified, modifier = Modifier.size(16.dp))
            Text(" Filter   ", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BoldGreen)
            Icon(painterResource(Res.drawable.sort_icon), null, tint = Color.Unspecified, modifier = Modifier.size(16.dp))
            Text(" Sort", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BoldGreen)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(filteredOrders) { order ->
                PurchaseOrderRow(
                    order = order,
                    isManager = state.role == EmployeeType.Manager,
                    onApprove = { viewModel.onEvent(PurchasesListEvent.UpdateOrderStatus(order.id, OrderStatus.Approved)) },
                    onReject = { viewModel.onEvent(PurchasesListEvent.UpdateOrderStatus(order.id, OrderStatus.Rejected)) },
                    onComplete = { viewModel.onEvent(PurchasesListEvent.UpdateOrderStatus(order.id, OrderStatus.Completed)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PurchaseOrderRow(
    order: PurchaseOrder,
    isManager: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onComplete: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Icon(painterResource(Res.drawable.purchases_icon), null, tint = BoldGreen, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Purchase: ${order.amount} ${order.subject} -", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text("Created: ${order.createdAt}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text("Requested By: ${order.requesterName}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text("Price: ${order.price} GEL", fontSize = 11.sp, fontFamily = FontFamily.Monospace)

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                if (isManager && order.status == OrderStatus.Pending) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onApprove, colors = ButtonDefaults.buttonColors(containerColor = BoldGreen), modifier = Modifier.height(32.dp)) { Text("Approve", fontSize = 10.sp) }
                        Button(onClick = onReject, colors = ButtonDefaults.buttonColors(containerColor = Color.Red), modifier = Modifier.height(32.dp)) { Text("Reject", fontSize = 10.sp) }
                    }
                } else if (isManager && order.status == OrderStatus.Approved) {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5B43)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Complete Order", fontSize = 12.sp, color = Color.White, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    val statusColor = when (order.status) {
                        OrderStatus.Pending -> SecondaryGray
                        OrderStatus.Approved -> Color(0xFFD98327)
                        OrderStatus.Rejected -> Color.Red
                        OrderStatus.Completed -> BoldGreen
                    }
                    Text("${order.status.name} ◷", color = statusColor, fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}