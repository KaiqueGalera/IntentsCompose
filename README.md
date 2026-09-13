# Conceitos envolvidos no projeto

### Grafo de navegação "para onde ir?"
-  Definido apenas 1 vez, no: MainNaviHost: SenderScreen e ReceiverScreen //nomes genéricos
-  Representa topologia completa

### Pilha de navegação "por onde eu já passei?"
- Muda a cada navigate() e popBackStack()
- No app de exemplo o caminho típico é: Home->AddWordScreen->Home

O **grafo** é o **mapa** com as duas **estações possíveis**
A **pilha** é o **trajeto** que o **usuário** de fato fez

## 1. Navigation Compose (grafo de rotas com `NavHost`)

Define as telas do app como um grafo de navegação, associando cada rota a um composable.

```kotlin
NavHost(navController = navHostController, startDestination = Screen.HomeScreen.route) {
    composable(route = Screen.HomeScreen.route) { backStackEntry -> ... }
    composable(
        route = "${Screen.AddWordScreen.route}/{currentText}",
        arguments = listOf(navArgument("currentText") { type = NavType.StringType })
    ) { backStackEntry -> ... }
}
```

## Rotas tipadas com `sealed class`

Evita strings soltas espalhadas pelo código, centralizando os nomes de rota.

```kotlin
sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object AddWordScreen : Screen("add_word_screen")
}
```

## Passagem de dado "para frente" -> argumento de rota

O valor viaja embutido na própria URL de navegação (por isso precisa de `Uri.encode`, já que a string pode conter espaços/caracteres especiais).

```kotlin
onAddWordClick = {
    navHostController.navigate("${Screen.AddWordScreen.route}/${Uri.encode(currentText.value)}")
}
```

```kotlin
AddWordScreen(
    currentText = backStackEntry.arguments?.getString("currentText") ?: "",
    ...
)
```

## Passagem de dado "para trás" <— `SavedStateHandle`

Como não existe `navigate()` de volta, o dado é depositado no `savedStateHandle` da entrada *anterior* da pilha (a de quem vai receber o valor), e só depois a pilha é desempilhada.

```kotlin
onConcatenateClick = { newWord ->
    navHostController.previousBackStackEntry?.savedStateHandle?.set(WORD_ADDED, newWord)
    navHostController.popBackStack()
}
```

```kotlin
val wordAdded = backStackEntry.savedStateHandle.get<String>(WORD_ADDED)
```

## `LaunchedEffect`(efeito colateral controlado)

Mutar estado (`currentText.value = ...`) e limpar a chave (`remove`) não pode acontecer direto no corpo do composable (rodaria a cada recomposição de forma imprevisível). O `LaunchedEffect(wordAdded)` só dispara quando `wordAdded` muda de valor.

```kotlin
LaunchedEffect(wordAdded) {
    if (wordAdded != null) {
        currentText.value =
            if (currentText.value.isEmpty()) wordAdded
            else "${currentText.value} $wordAdded"
        backStackEntry.savedStateHandle.remove<String>(WORD_ADDED)
    }
}
```

## `remember` vs `rememberSaveable` (sobrevivência de estado)

`remember` só sobrevive à recomposição; ao sair de `HomeScreen` para `AddWordScreen`, a composição é descartada e o estado se perde. `rememberSaveable` sobrevive à recriação da composição (e à rotação de tela), preservando a string acumulada.

```kotlin
val currentText = rememberSaveable { mutableStateOf("") }
```

## Separação de responsabilidades 

`AddWordScreen` é somente coleta de dado (não conhece a regra de concatenação); a lógica de negócio (concatenar) fica centralizada em `MainNavHost`, junto ao dono do estado (`HomeScreen`).

```kotlin
Button(onClick = { onConcatenateClick(newWord) }) { Text("Concatenar") }
```

```kotlin
currentText.value =
    if (currentText.value.isEmpty()) wordAdded
    else "${currentText.value} $wordAdded"
```

## Composables controlados (*state hoisting*)

`OutlinedTextField` com `readOnly = true` e `onValueChange = {}` exibe um valor vindo de fora sem permitir edição — o estado real mora fora da tela (em `MainNavHost`), e a tela só recebe e exibe.

```kotlin
OutlinedTextField(
    value = currentText,
    onValueChange = {},
    readOnly = true,
    label = { Text("String atual") }
)
```
