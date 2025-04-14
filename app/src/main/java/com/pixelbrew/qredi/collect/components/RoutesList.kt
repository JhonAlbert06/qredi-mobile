package com.pixelbrew.qredi.collect.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.collect.CollectViewModel
import com.pixelbrew.qredi.data.network.model.RouteModel
import kotlinx.coroutines.launch

@Composable
fun RoutesList(
    routes: List<RouteModel>,
    viewModel: CollectViewModel,
    onRouteSelected: () -> Unit
) {

    if (routes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LazyColumn {
        items(routes) { route ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            viewModel.downloadRoute(route.id)
                            onRouteSelected()
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