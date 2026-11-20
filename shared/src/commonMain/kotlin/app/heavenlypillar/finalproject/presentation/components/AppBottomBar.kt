package app.heavenlypillar.finalproject.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import app.heavenlypillar.finalproject.theme.SoftPink
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import finalproject.shared.generated.resources.*

enum class BottomTab(val route: String, val icon: DrawableResource) {
    Businesses("businesses", Res.drawable.businesses_icon),
    Purchases("purchases", Res.drawable.purchases_icon),
    Calendar("calendar", Res.drawable.calendar_icon),
    Staff("staff", Res.drawable.employees_icon),
    Analytics("analytics", Res.drawable.statistic_icon)
}

@Composable
fun AppBottomBar(
    currentRoute: String,
    role: EmployeeType,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember(role) {
        if (role == EmployeeType.Manager) BottomTab.entries
        else BottomTab.entries.filter { it != BottomTab.Analytics }
    }

    // Outer floating pill container
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        shape = RoundedCornerShape(32.dp),
        color = SoftPink,
        tonalElevation = 4.dp
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.height(64.dp),
            windowInsets = androidx.compose.foundation.layout.WindowInsets(0.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = currentRoute == tab.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            painter = painterResource(tab.icon),
                            contentDescription = tab.name,
                            modifier = Modifier.size(26.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BoldGreen,
                        unselectedIconColor = SecondaryGray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}