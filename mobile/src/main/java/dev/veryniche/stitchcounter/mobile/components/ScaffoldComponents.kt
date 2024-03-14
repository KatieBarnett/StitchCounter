package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.R

@OptIn(ExperimentalMaterial3Api::class)
private val topAppBarColors: TopAppBarColors
    @Composable
    get() = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsedTopAppBar(
    titleText: String,
    actions: @Composable RowScope.() -> Unit,
    onNavigation: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        actions = actions,
        colors = topAppBarColors,
        navigationIcon = {
            onNavigation?.let {
                NavigationIcon { onNavigation.invoke() }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandingTopAppBar(
    titleText: String,
    actions: @Composable RowScope.() -> Unit,
    onNavigation: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = {
            Text(
                text = titleText,
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        colors = topAppBarColors,
        actions = actions,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            onNavigation?.let {
                NavigationIcon { onNavigation.invoke() }
            }
        }
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
