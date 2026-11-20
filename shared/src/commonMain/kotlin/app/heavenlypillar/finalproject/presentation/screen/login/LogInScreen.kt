package app.heavenlypillar.finalproject.presentation.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.presentation.components.AppButton
import app.heavenlypillar.finalproject.presentation.components.AppTextField
import app.heavenlypillar.finalproject.presentation.screen.login.contract.LoginEffect
import app.heavenlypillar.finalproject.presentation.screen.login.contract.LoginEvent
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.DarkGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import finalproject.shared.generated.resources.Res
import finalproject.shared.generated.resources.administrator
import finalproject.shared.generated.resources.logo
import finalproject.shared.generated.resources.reg_employee
import finalproject.shared.generated.resources.sign_in

@Composable
fun LoginScreen(
    targetRole: EmployeeType,
    viewModel: LoginViewModel,
    onNavigateToBusinesses: (EmployeeType) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(targetRole) {
        viewModel.onEvent(LoginEvent.SetTargetRole(targetRole))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LoginEffect.NavigateToBusinesses -> onNavigateToBusinesses(effect.role)
                is LoginEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    val titleText = if (targetRole == EmployeeType.Manager) {
        stringResource(Res.string.administrator)
    } else {
        stringResource(Res.string.reg_employee)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(painter = painterResource(Res.drawable.logo), contentDescription = "Logo", modifier = Modifier.size(200.dp))
        Spacer(modifier = Modifier.height(28.dp))
        Text(text = titleText, color = DarkGreen, fontSize = 20.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 4.sp)
        Spacer(modifier = Modifier.height(36.dp))

        AppTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(LoginEvent.OnEmailChanged(it)) },
            placeholder = "Email@Gmail.Com",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(20.dp))

        AppTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(LoginEvent.OnPasswordChanged(it)) },
            placeholder = "Password",
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                Text(
                    text = if (passwordVisible) "HIDE" else "SHOW",
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .clickable { passwordVisible = !passwordVisible },
                    color = BoldGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        )
        Spacer(modifier = Modifier.height(32.dp))

        if (state.isLoading) {
            CircularProgressIndicator(color = BoldGreen, modifier = Modifier.size(36.dp))
        } else {
            AppButton(text = stringResource(Res.string.sign_in), onClick = { viewModel.onEvent(LoginEvent.OnSignInClicked) })
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}