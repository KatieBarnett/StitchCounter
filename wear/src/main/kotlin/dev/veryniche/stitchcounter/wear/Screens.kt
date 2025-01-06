package dev.veryniche.stitchcounter.wear

import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.data.models.ScreenOnState

enum class Screens(val pageContextDisplay: Int?) {
    About(R.string.app_name),
    WhatsNew(R.string.app_name),
    PhoneAppInfo(R.string.app_name),
    ProjectList(R.string.app_name),
    Project(null),
    Counter(R.string.context_counter),
    EditProject(R.string.context_project),
    EditCounter(R.string.context_counter),
    SelectProjectForTile(R.string.app_name),
    SelectCounterForTile(R.string.app_name)
}

fun Screens.showScreenAlwaysOn(screenOnState: ScreenOnState): Boolean {
    return when (this) {
        Screens.About -> false
        Screens.WhatsNew -> false
        Screens.ProjectList -> false
        Screens.PhoneAppInfo -> false
        Screens.Project -> screenOnState.projectScreenOn
        Screens.Counter -> screenOnState.counterScreenOn
        Screens.EditProject -> false
        Screens.EditCounter -> false
        Screens.SelectProjectForTile -> true
        Screens.SelectCounterForTile -> true
    }
}