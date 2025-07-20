package com.example.notasapp.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.notasapp.presentation.login.LoginScreen
import com.example.notasapp.presentation.login.RegisterScreen
import com.example.notasapp.presentation.notes.CreateNoteScreen
import com.example.notasapp.presentation.notes.FavoriteNotesScreen
import com.example.notasapp.presentation.notes.NoteDetailScreen
import com.example.notasapp.presentation.notes.NotesScreen
import com.example.notasapp.presentation.profile.ProfileScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val context: Context = LocalContext.current

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController, context)
        }

        composable("register") {
            RegisterScreen(navController, context)
        }

        composable("notes") {
            NotesScreen(navController, context)
        }

        composable("createNote") {
            CreateNoteScreen(navController, context)
        }

        composable("noteDetail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            NoteDetailScreen(navController, context, id)
        }

        composable("favorites") {
            FavoriteNotesScreen(navController, context)
        }

        composable("profile") {
            ProfileScreen(navController, context)
        }


    }
}
