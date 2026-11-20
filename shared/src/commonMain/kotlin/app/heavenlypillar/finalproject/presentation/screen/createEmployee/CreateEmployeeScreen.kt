package app.heavenlypillar.finalproject.presentation.screen.createEmployee

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
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.presentation.components.AppButton
import app.heavenlypillar.finalproject.presentation.components.AppTextField
import app.heavenlypillar.finalproject.presentation.screen.createEmployee.contract.CreateEmployeeEffect
import app.heavenlypillar.finalproject.presentation.screen.createEmployee.contract.CreateEmployeeEvent
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import finalproject.shared.generated.resources.*
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher

@Composable
fun CreateEmployeeScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateEmployeeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CreateEmployeeEffect.NavigateBack -> onNavigateBack()
                is CreateEmployeeEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
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
                    text = stringResource(Res.string.create_new_emplyee),
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

            val coroutineScope = rememberCoroutineScope()
            val launcher = rememberFilePickerLauncher(
                type = PickerType.Image,
                mode = PickerMode.Single,
                title = "Pick an Avatar"
            ) { file ->
                coroutineScope.launch {
                    val bytes = file?.readBytes()
                    viewModel.onEvent(CreateEmployeeEvent.OnAvatarSelected(bytes))
                }
            }

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
                        .clickable { launcher.launch() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.profile_icon),
                        contentDescription = "Upload Avatar",
                        tint = if (state.avatarBytes != null) BoldGreen else SecondaryGray,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LabeledInput(label = "Name") {
                        AppTextField(
                            value = state.name,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnNameChanged(it)) },
                            placeholder = "First Name"
                        )
                    }

                    LabeledInput(label = "Surname") {
                        AppTextField(
                            value = state.lastName,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnLastNameChanged(it)) },
                            placeholder = "Last Name"
                        )
                    }

                    LabeledInput(label = "Email") {
                        AppTextField(
                            value = state.email,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnEmailChanged(it)) },
                            placeholder = "work@email.com"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(Color(0xFFD98327))
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.details),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
                        spotColor = Color(0xFFF3D2AF),
                        ambientColor = Color(0xFFF3D2AF)
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFF3D2AF),
                        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LabeledInput(label = "Phone Number:") {
                        AppTextField(
                            value = state.phoneNumber,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnPhoneNumberChanged(it)) },
                            placeholder = "+995 500 000 000"
                        )
                    }

                    LabeledInput(label = "Address") {
                        AppTextField(
                            value = state.address,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnAddressChanged(it)) },
                            placeholder = "Residential Address"
                        )
                    }

                    LabeledInput(label = "Birthday") {
                        AppTextField(
                            value = state.birthday,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnBirthdayChanged(it)) },
                            placeholder = "YYYY-MM-DD"
                        )
                    }
                    LabeledInput(label = "Assigned Business:") {
                        Box {
                            AppTextField(
                                value = state.selectedBusinessName.ifEmpty { "Select Business" },
                                onValueChange = {},
                                placeholder = "Select Business",
                                readOnly = true
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { viewModel.onEvent(CreateEmployeeEvent.ToggleBusinessDropdown) }
                            )

                            DropdownMenu(
                                expanded = state.isBusinessDropdownExpanded,
                                onDismissRequest = { viewModel.onEvent(CreateEmployeeEvent.ToggleBusinessDropdown) }
                            ) {
                                state.availableBusinesses.forEach { business ->
                                    DropdownMenuItem(
                                        text = { Text(business.name, fontFamily = FontFamily.Monospace, color = BoldGreen) },
                                        onClick = { viewModel.onEvent(CreateEmployeeEvent.OnBusinessSelected(business.id, business.name)) }
                                    )
                                }
                            }
                        }
                    }

                    LabeledInput(label = "Start Date:") {
                        AppTextField(
                            value = state.startDate,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnStartDateChanged(it)) },
                            placeholder = "YYYY-MM-DD"
                        )
                    }

                    LabeledInput(label = "Position (Job Title):") {
                        AppTextField(
                            value = state.position,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnPositionChanged(it)) },
                            placeholder = "e.g. Barman, Shift Manager"
                        )
                    }

                    LabeledInput(label = "App Access Level:") {
                        Box {
                            AppTextField(
                                value = if (state.role == EmployeeType.Manager) "Manager (Admin Access)" else "Regular Employee",
                                onValueChange = {},
                                placeholder = "",
                                readOnly = true
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { viewModel.onEvent(CreateEmployeeEvent.ToggleRoleDropdown) }
                            )

                            DropdownMenu(
                                expanded = state.isRoleDropdownExpanded,
                                onDismissRequest = { viewModel.onEvent(CreateEmployeeEvent.ToggleRoleDropdown) }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Regular Employee", fontFamily = FontFamily.Monospace) },
                                    onClick = { viewModel.onEvent(CreateEmployeeEvent.OnRoleChanged(EmployeeType.RegEmployee)) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Manager (Admin Access)", fontFamily = FontFamily.Monospace, color = BoldGreen) },
                                    onClick = { viewModel.onEvent(CreateEmployeeEvent.OnRoleChanged(EmployeeType.Manager)) }
                                )
                            }
                        }
                    }

                    LabeledInput(label = "Regular Shift:") {
                        AppTextField(
                            value = state.shift,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnShiftChanged(it)) },
                            placeholder = "09:00 - 17:00"
                        )
                    }

                    LabeledInput(label = "Salary") {
                        AppTextField(
                            value = state.salary,
                            onValueChange = { viewModel.onEvent(CreateEmployeeEvent.OnSalaryChanged(it)) },
                            placeholder = "2500"
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AppButton(
                            text = stringResource(Res.string.create),
                            onClick = { viewModel.onEvent(CreateEmployeeEvent.OnCreateClicked) },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedButton(
                            onClick = { viewModel.onEvent(CreateEmployeeEvent.OnDiscardClicked) },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, BoldGreen)
                        ) {
                            Text(
                                text = stringResource(Res.string.discard),
                                color = BoldGreen,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LabeledInput(
    label: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = SecondaryGray,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}