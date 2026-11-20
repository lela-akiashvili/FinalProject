package app.heavenlypillar.finalproject.presentation.screen.roleAssertion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.presentation.components.AppButton
import app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract.RoleAssertionEffect
import app.heavenlypillar.finalproject.presentation.screen.roleAssertion.contract.RoleAssertionEvent
import app.heavenlypillar.finalproject.theme.DarkGreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import finalproject.shared.generated.resources.Res
import finalproject.shared.generated.resources.administrator
import finalproject.shared.generated.resources.login_As
import finalproject.shared.generated.resources.logo
import finalproject.shared.generated.resources.reg_employee

@Composable
fun RoleAssertionScreen(
    viewModel: RoleAssertionViewModel,
    onNavigateToLogin: (EmployeeType) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is RoleAssertionEffect.NavigateToLogin -> onNavigateToLogin(effect.role)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = "Manager App Logo",
            modifier = Modifier.size(220.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(Res.string.login_As),
            color = DarkGreen,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        AppButton(
            text = stringResource(Res.string.administrator),
            onClick = { viewModel.onEvent(RoleAssertionEvent.OnSelectRole(EmployeeType.Manager)) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppButton(
            text = stringResource(Res.string.reg_employee),
            onClick = { viewModel.onEvent(RoleAssertionEvent.OnSelectRole(EmployeeType.RegEmployee)) }
        )
    }
}