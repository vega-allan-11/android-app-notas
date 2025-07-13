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
fun CreateNoteScreen(navController: NavController, context: Context) {
    val vm: CreateNoteViewModel = viewModel()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val success by vm.success.collectAsState()
    val error by vm.error.collectAsState()

    if (success) {
        LaunchedEffect(Unit) {
            navController.popBackStack() // Vuelve a NotesScreen
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nueva Nota") })
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
            Button(onClick = {
                vm.createNote(context, title, content)
            }) {
                Text("Guardar")
            }
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}