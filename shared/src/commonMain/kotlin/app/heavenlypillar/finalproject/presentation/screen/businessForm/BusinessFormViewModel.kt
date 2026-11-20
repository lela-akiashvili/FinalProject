package app.heavenlypillar.finalproject.presentation.screen.businessForm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.model.Business
import app.heavenlypillar.finalproject.domain.repository.StorageRepository
import app.heavenlypillar.finalproject.domain.useCases.GetBusinessesUseCase
import app.heavenlypillar.finalproject.domain.useCases.SaveBusinessUseCase
import app.heavenlypillar.finalproject.presentation.screen.businessForm.contract.BusinessFormEffect
import app.heavenlypillar.finalproject.presentation.screen.businessForm.contract.BusinessFormEvent
import app.heavenlypillar.finalproject.presentation.screen.businessForm.contract.BusinessFormState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import io.ktor.util.decodeBase64Bytes
import io.ktor.util.encodeBase64
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class BusinessFormViewModel(
    private val saveBusinessUseCase: SaveBusinessUseCase,
    private val getBusinessesUseCase: GetBusinessesUseCase,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessFormState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<BusinessFormEffect>()
    val effects = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            try {
                val snapshot = Firebase.firestore.collection("employees").get()
                val managers = snapshot.documents.mapNotNull { doc ->
                    val role = doc.get<String>("role")
                    if (role == "Manager") {
                        val n = doc.get<String>("name")
                        val ln = doc.get<String>("lastName")
                        "$n $ln"
                    } else null
                }
                _uiState.update { it.copy(availableManagers = managers) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onEvent(event: BusinessFormEvent) {
        when (event) {
            is BusinessFormEvent.LoadBusiness -> loadBusiness(event.id)
            BusinessFormEvent.ToggleEditing -> _uiState.update { it.copy(isEditing = !it.isEditing) }
            is BusinessFormEvent.OnNameChanged -> _uiState.update { it.copy(name = event.value) }
            is BusinessFormEvent.OnAddressChanged -> _uiState.update { it.copy(address = event.value) }
            is BusinessFormEvent.OnLocationUrlChanged -> _uiState.update { it.copy(locationUrl = event.value) }
            is BusinessFormEvent.OnOpenHoursChanged -> _uiState.update { it.copy(openHours = event.value) }
            is BusinessFormEvent.OnEstablishmentDateChanged -> _uiState.update { it.copy(establishmentDate = event.value) }
            is BusinessFormEvent.OnManagerNameChanged -> _uiState.update { it.copy(managerName = event.value, isManagerDropdownExpanded = false) }
            is BusinessFormEvent.OnEmployeesSummaryChanged -> _uiState.update { it.copy(employeesSummary = event.value) }
            is BusinessFormEvent.OnMenuSummaryChanged -> _uiState.update { it.copy(menuSummary = event.value) }
            is BusinessFormEvent.OnServicesSummaryChanged -> _uiState.update { it.copy(servicesSummary = event.value) }
            is BusinessFormEvent.OnCoverSelected -> _uiState.update { it.copy(coverBytes = event.bytes) }
            BusinessFormEvent.ToggleManagerDropdown -> _uiState.update { it.copy(isManagerDropdownExpanded = !it.isManagerDropdownExpanded) }
            BusinessFormEvent.OnDiscardClicked -> handleDiscard()
            BusinessFormEvent.OnSaveClicked -> handleSave()
        }
    }

    private fun loadBusiness(id: String?) {
        if (id == null) {
            _uiState.update { it.copy(isEditing = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getBusinessesUseCase().fold(
                onSuccess = { businesses ->
                    val business = businesses.find { it.id == id }
                    if (business != null) {
                        _uiState.update {
                            it.copy(
                                id = business.id,
                                name = business.name,
                                address = business.address,
                                locationUrl = business.locationUrl,
                                photoUrl = business.photoUrl,
                                coverBytes = null,
                                openHours = business.openHours,
                                managerName = business.managerName,
                                menuSummary = business.menu.joinToString(", "),
                                servicesSummary = business.services.joinToString(", "),
                                isEditing = false,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, isEditing = true) }
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false, isEditing = true) }
                }
            )
        }
    }
    private fun handleDiscard() {
        viewModelScope.launch {
            _effects.emit(BusinessFormEffect.NavigateBack)
        }
    }

    private fun handleSave() {
        val state = _uiState.value
        if (state.isLoading) return
        val urlRegex = "^(https?://)?(www\\.)?(google\\.com/maps/.*|goo\\.gl/maps/.*|maps\\.app\\.goo\\.gl/.*)$".toRegex()
        val dateRegex = "^\\d{4}-\\d{2}-\\d{2}$".toRegex()

        viewModelScope.launch {
            if (state.name.isBlank()) {
                _effects.emit(BusinessFormEffect.ShowSnackbar("Business Name is mandatory."))
                return@launch
            }
            if (state.openHours.isBlank()) {
                _effects.emit(BusinessFormEffect.ShowSnackbar("Open Hours are mandatory."))
                return@launch
            }
            if (state.locationUrl.isNotBlank() && !state.locationUrl.matches(urlRegex)) {
                _effects.emit(BusinessFormEffect.ShowSnackbar("Please provide a valid Google Maps URL."))
                return@launch
            }
            if (state.establishmentDate.isNotBlank() && !state.establishmentDate.matches(dateRegex)) {
                _effects.emit(BusinessFormEffect.ShowSnackbar("Date must be YYYY-MM-DD."))
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val businessId = state.id.ifEmpty { Clock.System.now().toEpochMilliseconds().toString() }

            val uploadedCover = if (state.coverBytes != null) {
                "data:image/jpeg;base64," + state.coverBytes.encodeBase64()
            } else {
                state.photoUrl
            }

            val business = Business(
                id = businessId,
                name = state.name,
                address = state.address,
                locationUrl = state.locationUrl,
                photoUrl = uploadedCover,
                openHours = state.openHours,
                managerName = state.managerName,
                menu = state.menuSummary.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                services = state.servicesSummary.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            )

            saveBusinessUseCase(business).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(BusinessFormEffect.NavigateBack)
                },
                onFailure = { error ->
                    val message = error.message ?: "Failed to save business"
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                    _effects.emit(BusinessFormEffect.ShowSnackbar(message))
                }
            )
        }
    }
}