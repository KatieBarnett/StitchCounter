package dev.veryniche.stitchcounter.mobile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import dev.veryniche.stitchcounter.core.R
import dev.veryniche.stitchcounter.core.theme.Dimen
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.data.models.Project
import dev.veryniche.stitchcounter.mobile.BuildConfig
import dev.veryniche.stitchcounter.mobile.previews.PreviewComponent
import dev.veryniche.stitchcounter.mobile.ui.theme.StitchCounterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectItem(project: Project, onProjectClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        onClick = {
            project.id?.let {
                onProjectClick.invoke(it)
            }
        },
        modifier = modifier
    ) {
        Column(Modifier.padding(Dimen.spacingQuad)) {
            Text(
                text = if (BuildConfig.SHOW_IDS) {
                    "${project.name} (${project.id})"
                } else {
                    project.name
                },
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = if (project.counters.isEmpty()) {
                    stringResource(R.string.counters_label_zero)
                } else {
                    pluralStringResource(
                        R.plurals.counters_label,
                        project.counters.size,
                        project.counters.size
                    )
                },
            )
        }
    }
}

@PreviewComponent
@Composable
fun ProjectItemNoCountersPreview() {
    StitchCounterTheme {
        ProjectItem(Project(name = "Project name"), {}, Modifier)
    }
}

@PreviewComponent
@Composable
fun ProjectItemPreview() {
    StitchCounterTheme {
        ProjectItem(
            Project(
                name = "Project name",
                counters = listOf(
                    Counter(id = 0, name = "Counter 1"),
                    Counter(id = 1, name = "Counter 2")
                ),
            ),
            {},
            Modifier
        )
    }
}
