package app.heavenlypillar.finalproject.presentation.screen.businessForm

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.heavenlypillar.finalproject.presentation.components.AppButton
import app.heavenlypillar.finalproject.presentation.components.AppDetailsBox
import app.heavenlypillar.finalproject.presentation.components.AppTextField
import app.heavenlypillar.finalproject.presentation.screen.businessForm.contract.BusinessFormEffect
import app.heavenlypillar.finalproject.presentation.screen.businessForm.contract.BusinessFormEvent
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import finalproject.shared.generated.resources.Res
import finalproject.shared.generated.resources.location_icon
import finalproject.shared.generated.resources.edit_icon
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PlatformFile

@Composable
fun BusinessFormScreen(
    businessId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: BusinessFormViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState =
        remember { SnackbarHostState() }
    val coroutineScope =
        rememberCoroutineScope()

    val coverLauncher =
        rememberFilePickerLauncher(
            type = PickerType.Image,
            mode = PickerMode.Single
        ) { file: PlatformFile? ->
            coroutineScope.launch {
                viewModel.onEvent(
                    BusinessFormEvent.OnCoverSelected(file?.readBytes())
                )
            }
        }

    LaunchedEffect(businessId) {
        viewModel.onEvent(BusinessFormEvent.LoadBusiness(businessId))
        viewModel.effects.collect { effect ->
            when (effect) {
                BusinessFormEffect.NavigateBack -> onNavigateBack()
                is BusinessFormEffect.ShowSnackbar -> snackbarHostState.showSnackbar(
                    effect.message
                )
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.id.isEmpty()) "New Business" else state.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = BoldGreen
                )
                if (state.id.isNotEmpty()) {
                    Icon(
                        painter = painterResource(Res.drawable.edit_icon),
                        contentDescription = "Edit",
                        tint = if (state.isEditing) BoldGreen else SecondaryGray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                viewModel.onEvent(
                                    BusinessFormEvent.ToggleEditing
                                )
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE4D5C7),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (state.coverBytes != null) BoldGreen.copy(
                                    alpha = 0.2f
                                ) else Color(0xFFE0E0E0)
                            )
                            .clickable(enabled = state.isEditing) { coverLauncher.launch() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.photoUrl.isNotBlank() && state.coverBytes == null) {

                            AsyncImage(
                                model = state.photoUrl,
                                contentDescription = "Cover Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                        } else if (state.coverBytes != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(Res.drawable.location_icon),
                                    contentDescription = null,
                                    tint = BoldGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Cover Image Selected & Ready to Upload!",
                                    color = BoldGreen,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(Res.drawable.location_icon),
                                    contentDescription = null,
                                    tint = SecondaryGray,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap to Upload Cover",
                                    color = SecondaryGray,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    AppDetailsBox(
                        onEditClick = {
                            viewModel.onEvent(
                                BusinessFormEvent.ToggleEditing
                            )
                        },
                        isEditing = state.isEditing
                    ) {
                        FormField(
                            label = "Business Name",
                            value = state.name,
                            readOnly = !state.isEditing
                        ) {
                            viewModel.onEvent(
                                BusinessFormEvent.OnNameChanged(
                                    it
                                )
                            )
                        }
                        FormField(
                            label = "Location (Address)",
                            value = state.address,
                            readOnly = !state.isEditing
                        ) {
                            viewModel.onEvent(
                                BusinessFormEvent.OnAddressChanged(
                                    it
                                )
                            )
                        }

                        Text(
                            "Manager *",
                            fontSize = 12.sp,
                            color = SecondaryGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        BoldGreen,
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(16.dp)
                                    .clickable(enabled = state.isEditing) {
                                        viewModel.onEvent(
                                            BusinessFormEvent.ToggleManagerDropdown
                                        )
                                    }
                            ) {
                                Text(state.managerName.ifEmpty { "Select a Manager" })
                            }
                            DropdownMenu(
                                expanded = state.isManagerDropdownExpanded,
                                onDismissRequest = {
                                    viewModel.onEvent(
                                        BusinessFormEvent.ToggleManagerDropdown
                                    )
                                }
                            ) {
                                if (state.availableManagers.isEmpty()) {
                                    DropdownMenuItem(text = {
                                        Text(
                                            "No managers found",
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }, onClick = {})
                                } else {
                                    state.availableManagers.forEach { manager ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    manager,
                                                    fontFamily = FontFamily.Monospace,
                                                    color = BoldGreen
                                                )
                                            },
                                            onClick = {
                                                viewModel.onEvent(
                                                    BusinessFormEvent.OnManagerNameChanged(
                                                        manager
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        FormField(
                            label = "Location (Maps URL)",
                            value = state.locationUrl,
                            readOnly = !state.isEditing
                        ) {
                            viewModel.onEvent(
                                BusinessFormEvent.OnLocationUrlChanged(
                                    it
                                )
                            )
                        }
                        FormField(
                            label = "Open Hours:",
                            value = state.openHours,
                            readOnly = !state.isEditing
                        ) {
                            viewModel.onEvent(
                                BusinessFormEvent.OnOpenHoursChanged(
                                    it
                                )
                            )
                        }
                        FormField(
                            label = "Establishment Date:",
                            value = state.establishmentDate,
                            readOnly = !state.isEditing
                        ) {
                            viewModel.onEvent(
                                BusinessFormEvent.OnEstablishmentDateChanged(
                                    it
                                )
                            )
                        }
                        FormField(
                            label = "Menu (comma separated):",
                            value = state.menuSummary,
                            readOnly = !state.isEditing
                        ) {
                            viewModel.onEvent(
                                BusinessFormEvent.OnMenuSummaryChanged(
                                    it
                                )
                            )
                        }
                        FormField(
                            label = "Provided Services (comma separated):",
                            value = state.servicesSummary,
                            readOnly = !state.isEditing
                        ) {
                            viewModel.onEvent(
                                BusinessFormEvent.OnServicesSummaryChanged(
                                    it
                                )
                            )
                        }

                        if (state.isEditing) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    16.dp
                                )
                            ) {
                                AppButton(
                                    text = "Save",
                                    onClick = {
                                        viewModel.onEvent(
                                            BusinessFormEvent.OnSaveClicked
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedButton(
                                    onClick = {
                                        viewModel.onEvent(
                                            BusinessFormEvent.OnDiscardClicked
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        2.dp,
                                        BoldGreen
                                    )
                                ) {
                                    Text(
                                        text = "Discard",
                                        color = BoldGreen,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = SecondaryGray,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(4.dp))
        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = "",
            readOnly = readOnly
        )
    }
}