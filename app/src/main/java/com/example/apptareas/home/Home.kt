package com.example.apptareas.home
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptareas.Utils
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.login.SignUpScreen
import com.example.apptareas.models.Examenes
import com.example.apptareas.repository.Resources
import com.example.apptareas.ui.theme.AppTareasTheme
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

@Composable
fun Home(
    homeViewModel: HomeViewModel?,
    navToLoginPage: () -> Unit,
    navToExamPage: () -> Unit,
) {

    val CustomColorCasa = Color(0xFF96CD4F)
    val CustomColorTareas = Color(0xFF34B273)
    val CustomColorCompras = Color(0xFF009085)
    val CustomColorExamenes = Color(0xFF006B7B)

    Scaffold(

        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = {
                        homeViewModel?.signOut() // Llama a la función signOut del ViewModel
                        navToLoginPage()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                title = {
                    Text(text = "DoIt")
                }
            )
        }


    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Button(
                onClick = { /* Acción del botón */ },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColorCasa, // Color para "Home Issues"
                    contentColor = Color.Black // Texto negro para mayor contraste
                )
            ) {
                Text("Tareas de la casa")
            }

            Button(
                onClick = { navToExamPage() },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColorExamenes, // Color para "Examenes"
                    contentColor = Color.White // Texto blanco
                )
            ) {
                Text("Examenes")
            }

            Button(
                onClick = { /* Acción del botón */ },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColorCompras, // Color para "Compras"
                    contentColor = Color.White // Texto blanco
                )
            ) {
                Text("Compras")
            }

            Button(
                onClick = { /* Acción del botón */ },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CustomColorTareas, // Color para "Tareas de la Facu"
                    contentColor = Color.Black // Texto negro para contraste
                )
            ) {
                Text("Tareas de la Facu")
            }
        }
    }

    // Verifica si el usuario está autenticado
    LaunchedEffect(key1 = homeViewModel?.hasUser) {
        if (homeViewModel?.hasUser == false) {
            navToLoginPage.invoke()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Examen(
    homeViewModel: HomeViewModel?,
    onExamenClick: (id: String) -> Unit,
    navToDetailPage: () -> Unit,
    navToLoginPage: () -> Unit
) {
    // Usa el estado del ViewModel
    val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()

    var openDialog by remember { mutableStateOf(false) }
    var selectedExamen: Examenes? by remember { mutableStateOf(null) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        homeViewModel?.loadExamenes() // Cambiado de loadExamenes a loadNotes
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navToDetailPage() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar examen"
                )
            }
        },
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = {
                        homeViewModel?.signOut() // Llama a la función signOut del ViewModel
                        navToLoginPage()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                },
                title = {
                    Text(text = "Home")
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val examenesList = homeUiState.examenesList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // Cambiado de 'cells' a 'columns'
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(homeUiState.examenesList.data ?: emptyList()) { examen ->
                            ExamenItem(
                                examen = examen,
                                onLongClick = {
                                    openDialog = true
                                    selectedExamen = examen
                                },
                            ) {
                                onExamenClick.invoke(examen.documentId)
                            }
                        }
                    }

                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog = false
                            },
                            title = { Text(text = "¿Quieres borrar el examen?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedExamen?.documentId?.let {
                                            homeViewModel?.deleteExamen(it) // Usa la función deleteExamen del ViewModel
                                        }
                                        openDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Red
                                    )
                                ) {
                                    Text(text = "Borrar")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancelar")
                                }
                            }
                        )
                    }
                }

                else -> {
                    Text(
                        text = examenesList.throwable?.localizedMessage ?: "Error desconocido",
                        color = Color.Red
                    )
                }
            }
        }
    }

    // Verifica si el usuario está autenticado
    LaunchedEffect(key1 = homeViewModel?.hasUser) {
        if (homeViewModel?.hasUser == false) {
            navToLoginPage.invoke()
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExamenItem(
    examen: Examenes,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = onLongClick,
                onClick = onClick
            )
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Utils.colors[examen.colorIndex]
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = examen.materia,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = examen.description,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Start)
            )
            Text(
                text = formatData(examen.timestamp),
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.Start)
            )
        }
    }
}

private fun formatData(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yyyy hh:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}



@Preview
@Composable
fun PrevHomeScreen() {
    AppTareasTheme {
        Home(
            homeViewModel = null,
            navToLoginPage = {},
            navToExamPage =  {}
        )
    }
}

@Preview
@Composable
fun PrevExamenScreen() {
    AppTareasTheme {
        Examen(
            homeViewModel = null,
            onExamenClick = {},
            navToDetailPage = {},
            navToLoginPage = {}
        )
    }
}