package dev.veryniche.stitchcounter

import dev.veryniche.stitchcounter.data.models.ScreenOnState

enum class Screens(val pageContextDisplay: Int?) {
    About(R.string.app_name),
    ProjectList(R.string.app_name),
    Project(null),
    Counter(R.string.context_counter),
    EditProject(R.string.context_project),
    EditCounter(R.string.context_counter),
    SelectProjectForTile(R.string.context_select_project_for_tile),
    SelectCounterForTile(R.string.context_select_counter_for_tile)
}

fun Screens.showScreenAlwaysOn(screenOnState: ScreenOnState): Boolean {
    return when (this) {
        Screens.About -> false
        Screens.ProjectList -> false
        Screens.Project -> screenOnState.projectScreenOn
        Screens.Counter -> screenOnState.counterScreenOn
        Screens.EditProject -> false
        Screens.EditCounter -> false
        Screens.SelectProjectForTile -> true
        Screens.SelectCounterForTile -> true
    }
}