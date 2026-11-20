package app.heavenlypillar.finalproject.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import finalproject.shared.generated.resources.Res
import finalproject.shared.generated.resources.edit_icon
import org.jetbrains.compose.resources.painterResource

@Composable
fun AppDetailsBox(
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    isEditing: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
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
        Icon(
            painter = painterResource(Res.drawable.edit_icon),
            contentDescription = "Edit",
            tint = if (isEditing) Color.Gray else Color.Unspecified,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
                .clickable { onEditClick() }
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            content = content
        )
    }
}