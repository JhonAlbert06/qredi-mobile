package com.pixelbrew.qredi.collect

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.network.model.RouteModel
import kotlinx.coroutines.launch

@Composable
fun CollectScreen(viewModel: CollectViewModel, modifier: Modifier = Modifier) {
    val routes = viewModel.routes

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    ) {
        Collect(viewModel, modifier)
        Spacer(modifier = Modifier.height(8.dp))
        RoutesList(routes, modifier, viewModel)


    }
}

@Composable
fun Collect(viewModel: CollectViewModel, modifier: Modifier) {
    DownloadRoute(viewModel)
    Spacer(Modifier.height(8.dp))
    RoutesList(viewModel.routes, modifier, viewModel)
}

@Composable
fun RoutesList(routes: List<RouteModel>, modifier: Modifier, viewModel: CollectViewModel) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        items(routes) { route ->
            Card(
                modifier = modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            viewModel.downloadRoute(route.id)
                        }
                    }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(route.name)
                }
            }
        }
    }

}


@Composable
fun DownloadRoute(viewModel: CollectViewModel) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Rutas",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = {
                coroutineScope.launch {
                    viewModel.getRoutes()
                }
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00BCD4),
                contentColor = Color.Black,
                disabledContainerColor = Color(0x2C00BCD4),
                disabledContentColor = Color(0xFF0C0C0C)
            )
        ) {
            Text("Descargar")
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Download Route",
            )
        }

    }
}




