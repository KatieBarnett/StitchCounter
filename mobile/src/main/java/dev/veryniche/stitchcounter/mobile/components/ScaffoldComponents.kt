package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.R

@Composable
fun TopAppBarTitle(textResource: Int) {
    Text(
        text = stringResource(textResource),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
fun NavigationIcon(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick.invoke() }
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.navigate_back)
        )
    }
}

@Composable
fun AboutActionIcon(onClick: () -> Unit) {
    IconButton(
        onClick = { onClick.invoke() }
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = stringResource(id = R.string.navigate_about)
        )
    }
}
