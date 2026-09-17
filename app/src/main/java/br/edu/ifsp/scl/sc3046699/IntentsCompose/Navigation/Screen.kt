package br.edu.ifsp.scl.sc3046699.IntentsCompose.Navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object AddWordScreen : Screen("add_word_screen")
}