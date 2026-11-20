package app.heavenlypillar.finalproject.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.heavenlypillar.finalproject.domain.model.Employee
import app.heavenlypillar.finalproject.domain.model.EmployeeType
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import coil3.compose.AsyncImage
import finalproject.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun EmployeeCard(
    employee: Employee,
    viewerRole: EmployeeType,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            if (employee.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = employee.photoUrl,
                    contentDescription = "${employee.name} ${employee.lastName}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.profile_icon),
                    contentDescription = null,
                    tint = SecondaryGray,
                    modifier = Modifier.size(40.dp).align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "${employee.name} ${employee.lastName}".trim().ifEmpty { "Employee Name" },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color.Black
            )

            Text(
                text = employee.position.ifEmpty { "Staff Member" },
                fontSize = 13.sp,
                color = SecondaryGray,
                fontFamily = FontFamily.Monospace
            )

            if (viewerRole == EmployeeType.Manager) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.location_icon),
                        contentDescription = "Business Location",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = employee.businessName.ifEmpty { "Assigned Workplace" },
                        fontSize = 12.sp,
                        color = Color.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                Text(
                    text = employee.workEmail.ifEmpty { "email@workplace.com" },
                    fontSize = 12.sp,
                    color = SecondaryGray,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = employee.phoneNumber.ifEmpty { "+995 000 000 000" },
                    fontSize = 12.sp,
                    color = SecondaryGray,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        if (viewerRole == EmployeeType.Manager) {
            Icon(
                painter = painterResource(Res.drawable.edit_icon),
                contentDescription = "Edit Employee",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onEditClick)
            )
        }
    }
}