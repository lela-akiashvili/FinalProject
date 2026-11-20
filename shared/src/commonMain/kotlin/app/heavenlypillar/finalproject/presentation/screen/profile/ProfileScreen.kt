package app.heavenlypillar.finalproject.presentation.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.model.Vacation
import app.heavenlypillar.finalproject.domain.model.VacationType
import app.heavenlypillar.finalproject.presentation.components.AppButton
import app.heavenlypillar.finalproject.presentation.components.AppDetailsBox
import app.heavenlypillar.finalproject.presentation.components.AppTextField
import app.heavenlypillar.finalproject.presentation.screen.profile.contract.*
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import finalproject.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    targetEmployeeId: String?,
    onNavigateBack: () -> Unit,
    onSignedOut: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val canEdit = state.isEditing && (state.isOwnProfile || state.viewerRole == EmployeeType.Manager)

    val coroutineScope = rememberCoroutineScope()
    val avatarLauncher = rememberFilePickerLauncher(type = PickerType.Image, mode = PickerMode.Single) { file: PlatformFile? ->
        coroutineScope.launch { viewModel.onEvent(ProfileEvent.OnAvatarSelected(file?.readBytes())) }
    }

    LaunchedEffect(targetEmployeeId) {
        viewModel.onEvent(ProfileEvent.LoadProfile(targetEmployeeId))
        viewModel.effects.collect { effect ->
            when (effect) {
                ProfileEffect.NavigateBack -> onNavigateBack()
                ProfileEffect.NavigateToLogin -> onSignedOut()
                is ProfileEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding().imePadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.isOwnProfile) "My Profile" else "${state.name} ${state.lastName}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Black
                )
                Icon(
                    painter = painterResource(Res.drawable.profile_icon),
                    contentDescription = "Profile",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (state.avatarBytes != null) BoldGreen.copy(alpha = 0.2f) else Color(0xFFE0E0E0))
                        .clickable(enabled = state.isOwnProfile && canEdit) { avatarLauncher.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.profile_icon),
                        contentDescription = null,
                        tint = if (state.avatarBytes != null) BoldGreen else SecondaryGray,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProfileInputRow(label = "Name/Surname") {
                        AppTextField(
                            value = "${state.name} ${state.lastName}".trim(),
                            onValueChange = {},
                            placeholder = "Name",
                            readOnly = !canEdit
                        )
                    }

                    ProfileInputRow(label = "Email") {
                        AppTextField(
                            value = state.email,
                            onValueChange = {},
                            placeholder = "Email",
                            readOnly = true
                        )
                    }

                    if (state.isOwnProfile) {
                        ProfileInputRow(label = "Password") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BoldGreen, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Text("••••••••", fontFamily = FontFamily.Monospace, color = Color.Black)
                                Icon(
                                    painter = painterResource(Res.drawable.edit_icon),
                                    contentDescription = "Change Password",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .size(18.dp)
                                        .clickable { viewModel.onEvent(ProfileEvent.OpenChangePasswordDialog) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(if (state.selectedTab == ProfileTab.Details) Color(0xFFD98327) else Color(0xFFB0B0B0))
                        .clickable { viewModel.onEvent(ProfileEvent.SelectTab(ProfileTab.Details)) }
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text("Details", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }

                if (state.isOwnProfile) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(if (state.selectedTab == ProfileTab.VacationDays) Color(0xFF6B8E6B) else Color(0xFF8BA68B).copy(alpha = 0.6f))
                            .clickable { viewModel.onEvent(ProfileEvent.SelectTab(ProfileTab.VacationDays)) }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Vacation Days", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .border(1.dp, Color.Red, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                            .background(if (state.selectedTab == ProfileTab.Termination) Color.Red.copy(alpha = 0.1f) else Color.White)
                            .clickable { viewModel.onEvent(ProfileEvent.SelectTab(ProfileTab.Termination)) }
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Termination", color = Color.Red, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            when (state.selectedTab) {
                ProfileTab.Details -> {
                    AppDetailsBox(
                        onEditClick = { viewModel.onEvent(ProfileEvent.ToggleEditing) },
                        isEditing = canEdit
                    ) {
                        ProfileInputRow("Address") {
                            AppTextField(value = state.address, onValueChange = { viewModel.onEvent(ProfileEvent.OnAddressChanged(it)) }, placeholder = "Address", readOnly = !canEdit)
                        }
                        ProfileInputRow("Start Date:") {
                            AppTextField(value = state.startDate, onValueChange = {}, placeholder = "Start Date", readOnly = true)
                        }
                        ProfileInputRow("Phone Number:") {
                            AppTextField(value = state.phoneNumber, onValueChange = { viewModel.onEvent(ProfileEvent.OnPhoneChanged(it)) }, placeholder = "+995...", readOnly = !canEdit)
                        }
                        ProfileInputRow("Birthday") {
                            AppTextField(value = state.birthday, onValueChange = { viewModel.onEvent(ProfileEvent.OnBirthdayChanged(it)) }, placeholder = "Birthday", readOnly = !canEdit)
                        }
                        ProfileInputRow("Preferred Language") {
                            AppTextField(value = state.preferredLanguage, onValueChange = { viewModel.onEvent(ProfileEvent.OnLanguageChanged(it)) }, placeholder = "English", readOnly = !canEdit)
                        }

                        if (canEdit) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AppButton(text = "Save", onClick = { viewModel.onEvent(ProfileEvent.SaveProfileClicked) }, modifier = Modifier.weight(1f))
                                OutlinedButton(
                                    onClick = { viewModel.onEvent(ProfileEvent.DiscardProfileClicked) },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, BoldGreen)
                                ) {
                                    Text("Discard", color = BoldGreen, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
                ProfileTab.VacationDays -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp), spotColor = Color(0xFFF3D2AF))
                            .background(Color.White, RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                            .border(1.dp, Color(0xFFF3D2AF), RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                Icon(painterResource(Res.drawable.filter_icon), null, tint = Color.Unspecified, modifier = Modifier.size(16.dp))
                                Text(" Filter   ", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BoldGreen)
                                Icon(painterResource(Res.drawable.sort_icon), null, tint = Color.Unspecified, modifier = Modifier.size(16.dp))
                                Text(" Sort", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = BoldGreen)
                            }
                            state.vacations.forEach { vac -> VacationItemRow(vacation = vac) }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painterResource(Res.drawable.purchases_icon), null, tint = BoldGreen, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Total PTO Days Left - ${state.ptoDaysLeft}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(onClick = { viewModel.onEvent(ProfileEvent.OpenAddVacationDialog) }, shape = RoundedCornerShape(12.dp), border = androidx.compose.foundation.BorderStroke(1.5.dp, BoldGreen)) {
                                    Text("Add Day-Off", color = BoldGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                ProfileTab.Termination -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp), spotColor = Color(0xFFF3D2AF))
                            .background(Color.White, RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                            .border(1.dp, Color(0xFFF3D2AF), RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(
                                text = "Termination Of This Account Will Result Of Restricting Person From Further Accessing The Application/Website.\n\nWe Will Be Deleting All Their Relevant Data And Associated Work Email.\n\nNote: Purchase Orders And Taken Day-Offs Will Still Be Part Of Statistics Calculation",
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 18.sp,
                                color = Color.Black
                            )
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                Button(
                                    onClick = { viewModel.onEvent(ProfileEvent.OpenTerminationDialog) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.width(140.dp).height(44.dp)
                                ) {
                                    Text("Proceed", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            if (state.isOwnProfile) {
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedButton(
                    onClick = { viewModel.onEvent(ProfileEvent.SignOutClicked) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD32F2F)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text(
                        text = "Sign Out",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (state.isAddVacationDialogOpen) {
        AddVacationDialog(
            onDismiss = { viewModel.onEvent(ProfileEvent.CloseAddVacationDialog) },
            onSubmit = { type, s, e, count -> viewModel.onEvent(ProfileEvent.SubmitVacationRequest(type, s, e, count)) }
        )
    }

    if (state.isTerminationConfirmDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(ProfileEvent.CloseTerminationDialog) },
            title = { Text("Confirm Termination", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to terminate this account? This cannot be undone.", fontFamily = FontFamily.Monospace) },
            confirmButton = {
                Button(onClick = { viewModel.onEvent(ProfileEvent.ConfirmTermination) }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Terminate", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.onEvent(ProfileEvent.CloseTerminationDialog) }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun VacationItemRow(vacation: Vacation) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.purchases_icon),
            contentDescription = null,
            tint = BoldGreen,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = "${vacation.type.name} - ${vacation.startDate} - ${vacation.endDate} (${vacation.daysCount} Days)",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (vacation.approvedBy.isNotEmpty()) "Approved By ${vacation.approvedBy}" else "Pending Approval...",
                fontSize = 11.sp,
                color = SecondaryGray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun ProfileInputRow(label: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, fontSize = 11.sp, color = SecondaryGray, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(2.dp))
        content()
    }
}

@Composable
private fun AddVacationDialog(onDismiss: () -> Unit, onSubmit: (VacationType, String, String, Int) -> Unit) {
    var startDate by remember { mutableStateOf("2026/12/23") }
    var endDate by remember { mutableStateOf("2027/01/01") }
    var days by remember { mutableStateOf("9") }
    var selectedType by remember { mutableStateOf(VacationType.DayOff) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Request Day-Off", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTextField(value = startDate, onValueChange = { startDate = it }, placeholder = "Start (YYYY/MM/DD)")
                AppTextField(value = endDate, onValueChange = { endDate = it }, placeholder = "End (YYYY/MM/DD)")
                AppTextField(value = days, onValueChange = { days = it }, placeholder = "Days count")
            }
        },
        confirmButton = { Button(onClick = { onSubmit(selectedType, startDate, endDate, days.toIntOrNull() ?: 1) }, colors = ButtonDefaults.buttonColors(containerColor = BoldGreen)) { Text("Submit") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}