package com.example.notasapp.presentation.notes

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteNotesScreen(navController: NavController, context: Context) {
    val vm: NotesViewModel = viewModel()

    val notes by vm.notes.collectAsState()
    val error by vm.error.collectAsState()

    // Filtra solo favoritos
    val favoriteNotes = notes.filter { it.is_favorite }

    LaunchedEffect(Unit) {
        vm.loadNotes(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (error != null) {
                Text(error ?: "", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (favoriteNotes.isEmpty()) {
                Text("No tienes notas favoritas.")
            } else {
                LazyColumn {
                    items(favoriteNotes) { note ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            navController.navigate("noteDetail/${note.id}")
                                        }
                                ) {
                                    Text(note.title, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(note.content, style = MaterialTheme.typography.bodyMedium)
                                }

                                IconButton(onClick = {
                                    vm.toggleFavorite(note.id, context)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Quitar favorito"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
