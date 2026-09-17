package br.edu.ifsp.scl.sc3046699.IntentsCompose.ui.composable

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.edu.ifsp.scl.sc3046699.IntentsCompose.ui.theme.NavigationComposeTheme

@Composable
fun HomeScreen(
    currentText: String,
    modifier: Modifier,
    onResetClick: () -> Unit,
    onAddWordClick: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentText,
            onValueChange = {},
            readOnly = true,
            label = { Text("String atual") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onAddWordClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar palavra")
        }
        Button(
            onClick = onResetClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reiniciar")
        }
    }
}

@Preview(name = "Light mode", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(name = "Dark mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview() {
    NavigationComposeTheme {
        Surface {
            HomeScreen(
                currentText = "Olá mundo",
                modifier = Modifier,
                onResetClick = {},
                onAddWordClick = {}
            )
        }
    }
}