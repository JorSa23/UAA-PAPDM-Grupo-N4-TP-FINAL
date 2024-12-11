package com.example.apptareas.detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptareas.Utils
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.ui.theme.AppTareasTheme
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DetailScreen(
    detailViewModel: DetailViewModel?,
    examenId: String,
    onNavigate: () -> Unit
) {
    val detailUiState = detailViewModel?.detailUiState ?: DetailUiState()
    val isFormsNotBlank = detailUiState.description.isNotBlank() &&
            detailUiState.materia.isNotBlank()

    val selectedColor by animateColorAsState(
        targetValue = Utils.colors[detailUiState.colorIndex]
    )
    val isExamenIdNotBlank = examenId.isNotBlank()
    val icon = if (isExamenIdNotBlank) Icons.Default.Refresh else Icons.Default.Check

    // Usar SnackbarHostState en lugar de scaffoldState
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        if (isExamenIdNotBlank) {
            detailViewModel?.getExamen(examenId)
        } else {
            detailViewModel?.resetState()
        }
    }

    Scaffold(
        floatingActionButton = {
                AnimatedVisibility(visible =  isFormsNotBlank) {
                    FloatingActionButton(
                        onClick = {
                        if (isExamenIdNotBlank) {
                            detailViewModel?.updateExamen(examenId)
                        } else {
                            detailViewModel?.addExamen()
                        }

                        // Mostrar snackbar
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isExamenIdNotBlank) "Nota Editada Correctamente" else "Nota Agregada Correctamente"
                            )
                        }
                    }
            ) {
                Icon(imageVector = icon, contentDescription = null)
            }
                }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = selectedColor)
                .padding(padding)
        ) {
            if (detailUiState.examenAddedStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Nota Agregada Correctamente")
                    detailViewModel?.resetExamenAddedStatus()
                    onNavigate.invoke()
                }
            }
            if (detailUiState.updateExamenStatus) {
                scope.launch {
                    snackbarHostState.showSnackbar("Nota Editada Correctamente")
                    detailViewModel?.resetExamenAddedStatus()
                    onNavigate.invoke()
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                contentPadding = PaddingValues(
                    vertical = 16.dp,
                    horizontal = 8.dp
                )
            ) {
                itemsIndexed(Utils.colors) { colorIndex, color ->
                    ColorItem(color = color) {
                        detailViewModel?.onColorChange(colorIndex)
                    }
                }
            }

            // Uso de OutlinedTextField dentro de una columna
            OutlinedTextField(
                value = detailUiState.materia,
                onValueChange = {
                    detailViewModel?.onMateriaChange(it)
                },
                label = { Text(text = "Materia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.description,
                onValueChange = {
                    detailViewModel?.onDescriptionChange(it)
                },
                label = { Text(text = "Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.fecha,
                onValueChange = {
                    detailViewModel?.onFechaChange(it)
                },
                label = { Text(text = "Fecha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.dia,
                onValueChange = {
                    detailViewModel?.onDiaChange(it)
                },
                label = { Text(text = "Dia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            OutlinedTextField(
                value = detailUiState.hora,
                onValueChange = {
                    detailViewModel?.onHoraChange(it)
                },
                label = { Text(text = "Hora") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun ColorItem(
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = color,
        shape = CircleShape,
        modifier = Modifier
            .padding(8.dp)
            .size(36.dp)
            .clickable {
                onClick.invoke()
            },
        border = BorderStroke(2.dp, Color.Black) // Usar Color de Compose
    ) { }
}




















