package app.heavenlypillar.finalproject.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.heavenlypillar.finalproject.theme.BoldGreen
import app.heavenlypillar.finalproject.theme.SecondaryGray
import app.heavenlypillar.finalproject.theme.SoftPink
import org.jetbrains.compose.resources.painterResource
import finalproject.shared.generated.resources.Res
import finalproject.shared.generated.resources.search_icon

@Composable
fun AppSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SoftPink)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                color = BoldGreen,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            ),
            singleLine = true,
            cursorBrush = SolidColor(BoldGreen),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = SecondaryGray,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                innerTextField()
            }
        )

        Icon(
            painter = painterResource(Res.drawable.search_icon),
            contentDescription = "Search",
            tint = SecondaryGray,
            modifier = Modifier.size(20.dp)
        )
    }
}