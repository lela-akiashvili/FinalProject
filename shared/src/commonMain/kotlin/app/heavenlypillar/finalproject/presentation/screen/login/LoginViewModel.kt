package app.heavenlypillar.finalproject.presentation.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.domain.useCases.LoginUseCase
import app.heavenlypillar.finalproject.presentation.screen.login.contract.LoginEffect
import app.heavenlypillar.finalproject.presentation.screen.login.contract.LoginEvent
import app.heavenlypillar.finalproject.presentation.screen.login.contract.LoginState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<LoginEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SetTargetRole -> {
                _uiState.update { it.copy(role = event.role) }
            }
            is LoginEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email, errorMessage = null) }
            }
            is LoginEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, errorMessage = null) }
            }
            is LoginEvent.OnSignInClicked -> {
                performSignIn()
            }
            is LoginEvent.OnClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun performSignIn() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = loginUseCase(
                email = currentState.email,
                password = currentState.password,
                expectedRole = currentState.role
            )

            result.fold(
                onSuccess = { employee ->
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(LoginEffect.NavigateToBusinesses(employee.role))
                },
                onFailure = { error ->
                    val message = error.message ?: "Authentication failed. Please verify your credentials."
                    _uiState.update { it.copy(isLoading = false, errorMessage = message) }
                    _effects.emit(LoginEffect.ShowSnackbar(message))
                }
            )
        }
    }
}