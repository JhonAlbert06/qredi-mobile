package com.pixelbrew.qredi.ui.collect.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelbrew.qredi.data.network.model.RouteModelRes
import com.pixelbrew.qredi.ui.collect.CollectViewModel
import kotlinx.coroutines.launch

@Composable
fun RoutesList(
    routes: List<RouteModelRes>,
    viewModel: CollectViewModel,
    onRouteSelected: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    if (routes.isEmpty()) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn {
            items(routes) { route ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            coroutineScope.launch {
                                viewModel.downloadRoute(route.id)
                                onRouteSelected()
                            }
                        },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(route.name)
                    }
                }
            }
        }
    }
}