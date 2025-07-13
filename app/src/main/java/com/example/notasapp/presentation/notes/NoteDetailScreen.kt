package com.example.notasapp.presentation.notes

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(navController: NavController, context: Context, noteId: Int) {
    val vm: NoteDetailViewModel = viewModel()

    val note by vm.note.collectAsState()
    val deleted by vm.deleted.collectAsState()
    val updated by vm.updated.collectAsState()
    val error by vm.error.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        vm.loadNote(context, noteId)
    }

    LaunchedEffect(note) {
        title = note?.title ?: ""
        content = note?.content ?: ""
    }

    if (deleted || updated) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalle de Nota") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Contenido") })
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(onClick = {
                    vm.updateNote(context, noteId, title, content)
                }) {
                    Text("Guardar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedButton(onClick = {
                    vm.deleteNote(context, noteId)
                }) {
                    Text("Eliminar")
                }
            }
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
