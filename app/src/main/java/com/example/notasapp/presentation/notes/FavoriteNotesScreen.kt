package com.example.notasapp.presentation.notes

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notasapp.data.model.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteNotesScreen(navController: NavController, context: Context) {
    val vm: NotesViewModel = viewModel()
    val notes by vm.notes.collectAsState()
    val error by vm.error.collectAsState()
    val currentOrder by vm.currentOrder.collectAsState()

    val favoriteNotes = notes.filter { it.is_favorite == true }

    var showGrid by remember { mutableStateOf(false) }
    var expandedOrder by remember { mutableStateOf(false) }

    val selectedNotes = remember { mutableStateListOf<Note>() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadNotes(context)
    }

    val backgroundGradient = Brush.verticalGradient(
        listOf(Color(0xFF1C1B2F), Color(0xFF2C2B3E))
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedNotes.isEmpty()) "Favoritos"
                        else "${selectedNotes.size} seleccionadas",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    if (selectedNotes.isEmpty()) {
                        IconButton(onClick = { showGrid = !showGrid }) {
                            Icon(
                                imageVector = if (showGrid) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    } else {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2C2B3E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(padding)
                .padding(16.dp)
        ) {
            if (favoriteNotes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes notas favoritas.", color = Color.LightGray)
                }
                return@Column
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ExposedDropdownMenuBox(
                    expanded = expandedOrder,
                    onExpandedChange = { expandedOrder = !expandedOrder }
                ) {
                    TextField(
                        value = "Ordenar por: ${if (currentOrder == "title") "Título" else "Fecha"}",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOrder) },
                        modifier = Modifier.menuAnchor().weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            cursorColor = Color.White
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedOrder,
                        onDismissRequest = { expandedOrder = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Título") },
                            onClick = {
                                vm.toggleOrder("title", context)
                                expandedOrder = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Fecha") },
                            onClick = {
                                vm.toggleOrder("created_at", context)
                                expandedOrder = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = showGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoriteNotes) { note ->
                        NoteCard(
                            note = note,
                            isSelected = selectedNotes.contains(note),
                            onClick = {
                                if (selectedNotes.isNotEmpty()) toggleNoteSelection(selectedNotes, note)
                                else navController.navigate("noteDetail/${note.id}")
                            },
                            onLongClick = { toggleNoteSelection(selectedNotes, note) },
                            onFavorite = { vm.toggleFavorite(note.id, context) },
                            onShare = { shareNoteContent(context, note) }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = !showGrid) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(favoriteNotes) { note ->
                        NoteCard(
                            note = note,
                            isSelected = selectedNotes.contains(note),
                            onClick = {
                                if (selectedNotes.isNotEmpty()) toggleNoteSelection(selectedNotes, note)
                                else navController.navigate("noteDetail/${note.id}")
                            },
                            onLongClick = { toggleNoteSelection(selectedNotes, note) },
                            onFavorite = { vm.toggleFavorite(note.id, context) },
                            onShare = { shareNoteContent(context, note) }
                        )
                    }
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("¿Eliminar notas?") },
                    text = { Text("¿Deseas eliminar las notas seleccionadas? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedNotes.forEach { vm.deleteNote(it.id, context) }
                            selectedNotes.clear()
                            showDeleteDialog = false
                        }) {
                            Text("Eliminar", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ✅ Funciones auxiliares con nombres únicos para evitar conflictos

private fun toggleNoteSelection(selectedNotes: MutableList<Note>, note: Note) {
    if (selectedNotes.contains(note)) {
        selectedNotes.remove(note)
    } else {
        selectedNotes.add(note)
    }
}

private fun shareNoteContent(context: Context, note: Note) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "${note.title}\n\n${note.content}")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
