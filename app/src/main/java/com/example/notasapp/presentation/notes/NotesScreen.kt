package com.example.notasapp.presentation.notes

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, context: Context) {
    val vm: NotesViewModel = viewModel()

    val notes by vm.notes.collectAsState()
    val error by vm.error.collectAsState()
    val searchText by vm.searchText.collectAsState()
    val currentOrder by vm.currentOrder.collectAsState()
    val ascending by vm.ascending.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadNotes(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Notas") },
                actions = {
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(Icons.Default.Star, contentDescription = "Ver favoritos")
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("createNote")
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // 🔍 Campo de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = { vm.updateSearch(it, context) },
                label = { Text("Buscar notas") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔃 Botones de ordenamiento verticales
            Column {
                Button(
                    onClick = { vm.toggleOrder("title", context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ordenar por Título")
                    if (currentOrder == "title") {
                        Icon(
                            imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { vm.toggleOrder("created_at", context) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ordenar por Fecha")
                    if (currentOrder == "created_at") {
                        Icon(
                            imageVector = if (ascending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ⚠️ Mensaje de error
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 📝 Lista de notas
            LazyColumn {
                items(notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate("noteDetail/${note.id}")
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(note.title, style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { vm.toggleFavorite(note.id, context) }) {
                                    if (note.is_favorite == true) {
                                        Icon(Icons.Default.Star, contentDescription = "Quitar de favoritos")
                                    } else {
                                        Icon(Icons.Outlined.StarBorder, contentDescription = "Agregar a favoritos")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
