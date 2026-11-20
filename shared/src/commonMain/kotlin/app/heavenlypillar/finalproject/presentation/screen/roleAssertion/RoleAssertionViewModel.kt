package app.heavenlypillar.finalproject.presentation.screen.roleAssertion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract.RoleAssertionEffect
import app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract.RoleAssertionEvent
import app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract.RoleAssertionState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoleAssertionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RoleAssertionState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<RoleAssertionEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: RoleAssertionEvent) {
        when (event) {
            is RoleAssertionEvent.OnSelectRole -> handleRoleSelection(event.role)
        }
    }

    private fun handleRoleSelection(role: EmployeeType) {
        _uiState.update { it.copy(selectedRole = role) }
        viewModelScope.launch {
            _effects.emit(RoleAssertionEffect.NavigateToLogin(role))
        }
    }
}