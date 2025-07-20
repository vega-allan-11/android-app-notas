package com.example.notasapp.presentation.notes

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.notasapp.data.model.Note
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, context: Context) {
    val vm: NotesViewModel = viewModel()
    val notes by vm.notes.collectAsState()
    val error by vm.error.collectAsState()
    val searchText by vm.searchText.collectAsState()
    val currentOrder by vm.currentOrder.collectAsState()
    val ascending by vm.ascending.collectAsState()

    var showGrid by remember { mutableStateOf(false) }
    var expandedOrder by remember { mutableStateOf(false) }

    val selectedNotes = remember { mutableStateListOf<Note>() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val localContext = LocalContext.current

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
                        if (selectedNotes.isEmpty()) "Mis Notas"
                        else "${selectedNotes.size} seleccionadas",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2C2B3E)),
                actions = {
                    if (selectedNotes.isEmpty()) {
                        IconButton(onClick = { navController.navigate("favorites") }) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                        }
                        IconButton(onClick = { navController.navigate("profile") }) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    } else {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedNotes.isEmpty()) {
                FloatingActionButton(onClick = {
                    navController.navigate("createNote")
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { vm.updateSearch(it, context) },
                label = { Text("Buscar notas") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.LightGray,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.LightGray,
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

                IconButton(onClick = { showGrid = !showGrid }) {
                    Icon(
                        imageVector = if (showGrid) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val displayedNotes = notes

            AnimatedVisibility(visible = showGrid) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedNotes) { note ->
                        NoteCard(
                            note = note,
                            isSelected = selectedNotes.contains(note),
                            onClick = {
                                if (selectedNotes.isNotEmpty()) toggleSelection(selectedNotes, note)
                                else navController.navigate("noteDetail/${note.id}")
                            },
                            onLongClick = { toggleSelection(selectedNotes, note) },
                            onFavorite = { vm.toggleFavorite(note.id, context) },
                            onShare = { shareNote(localContext, note) }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = !showGrid) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedNotes) { note ->
                        NoteCard(
                            note = note,
                            isSelected = selectedNotes.contains(note),
                            onClick = {
                                if (selectedNotes.isNotEmpty()) toggleSelection(selectedNotes, note)
                                else navController.navigate("noteDetail/${note.id}")
                            },
                            onLongClick = { toggleSelection(selectedNotes, note) },
                            onFavorite = { vm.toggleFavorite(note.id, context) },
                            onShare = { shareNote(localContext, note) }
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
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavorite: () -> Unit,
    onShare: () -> Unit
) {
    val background = if (isSelected) Color(0xFF5A4A7F) else Color(0xFF3A3A4A)

    val inputFormats = remember {
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        ).map { pattern ->
            SimpleDateFormat(pattern, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }
    }

    val outputFormat = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    }

    val formattedDate = remember(note.created_at) {
        val parsedDate = inputFormats.firstNotNullOfOrNull { format ->
            runCatching { format.parse(note.created_at) }.getOrNull()
        }
        parsedDate?.let { outputFormat.format(it) } ?: "Fecha inválida"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp) // Asegura espacio suficiente en grid
            .animateContentSize()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top // Alineación superior para mantener orden
            ) {
                Text(
                    note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(36.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = onFavorite,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            if (note.is_favorite == true) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorito",
                            tint = Color.Yellow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                maxLines = 4 // Limita texto para evitar sobrecarga
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Creado el $formattedDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
                IconButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.White)
                }
            }
        }
    }

}

fun toggleSelection(selectedNotes: MutableList<Note>, note: Note) {
    if (selectedNotes.contains(note)) {
        selectedNotes.remove(note)
    } else {
        selectedNotes.add(note)
    }
}

fun shareNote(context: Context, note: Note) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "${note.title}\n\n${note.content}")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
