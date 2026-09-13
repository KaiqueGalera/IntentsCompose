package br.edu.ifsp.scl.sc3046699.navigationcompose.Navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.edu.ifsp.scl.sc3046699.navigationcompose.ui.composable.AddWordScreen
import br.edu.ifsp.scl.sc3046699.navigationcompose.ui.composable.HomeScreen

private const val WORD_ADDED = "word_added"

@Composable
fun MainNavHost(navHostController: NavHostController, modifier: Modifier) {
    NavHost(navController = navHostController, startDestination = Screen.HomeScreen.route) {
        // Criando as rotas do grafo
        composable(route = Screen.HomeScreen.route) { backStackEntry ->
            // String acumulada, mantida enquanto a entrada de HomeScreen permanecer na pilha
            val currentText = rememberSaveable() { mutableStateOf("") }

            // Recebendo a palavra devolvida por AddWordScreen (se houver)
            val wordAdded = backStackEntry.savedStateHandle.get<String>(WORD_ADDED)

            LaunchedEffect(wordAdded) {
                if (wordAdded != null) {
                    // Regra de concatenação
                    currentText.value =
                        if (currentText.value.isEmpty()) wordAdded
                        else "${currentText.value} $wordAdded"
                    backStackEntry.savedStateHandle.remove<String>(WORD_ADDED)
                }
            }

            HomeScreen(
                currentText = currentText.value,
                modifier = modifier,
                onResetClick = { currentText.value = "" },
                onAddWordClick = {
                    navHostController.navigate("${Screen.AddWordScreen.route}/${Uri.encode(currentText.value)}")
                }
            )
        }
        // O parâmetro currentText será passado de HomeScreen para AddWordScreen como argumento da rota
        composable(
            route = "${Screen.AddWordScreen.route}/{currentText}",
            arguments = listOf(
                navArgument("currentText") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Recuperando o argumento passado na rota e passando para AddWordScreen como argumento
            AddWordScreen(
                currentText = backStackEntry.arguments?.getString("currentText") ?: "",
                modifier = modifier,
                onConcatenateClick = { newWord ->
                    // Enviando lambda no onConcatenateClick que deve ser executado ao clicar no botão Concatenar
                    // Guarda apenas a palavra digitada na entrada anterior da pilha (HomeScreen) e volta para ela
                    navHostController.previousBackStackEntry?.savedStateHandle?.set(WORD_ADDED, newWord)
                    navHostController.popBackStack()
                }
            )
        }
    }
}