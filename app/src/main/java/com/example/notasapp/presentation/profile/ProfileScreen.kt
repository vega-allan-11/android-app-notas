package com.example.notasapp.presentation.profile

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, context: Context) {
    val vm: ProfileViewModel = viewModel()

    LaunchedEffect(Unit) {
        vm.loadProfile(context)
    }

    val user = vm.user.value

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            email = it.email
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Perfil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // 🔹 Sección 1: Nombre y Email
            Text("Información del perfil", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    vm.updateProfile(
                        context,
                        name,
                        email,
                        currentPassword = "",
                        password = "",
                        confirmPassword = ""
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔐 Sección 2: Cambiar contraseña
            Text("Cambiar contraseña", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Contraseña actual") },
                visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (showCurrent) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { showCurrent = !showCurrent }) {
                        Icon(icon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva contraseña") },
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(icon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar nueva contraseña") },
                visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    IconButton(onClick = { showConfirm = !showConfirm }) {
                        Icon(icon, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    vm.updateProfile(
                        context,
                        name = "",
                        email = "",
                        currentPassword = currentPassword,
                        password = newPassword,
                        confirmPassword = confirmPassword
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar contraseña")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🔘 Logout
            Button(
                onClick = {
                    vm.logout(context) {
                        navController.navigate("login") {
                            popUpTo("profile") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
