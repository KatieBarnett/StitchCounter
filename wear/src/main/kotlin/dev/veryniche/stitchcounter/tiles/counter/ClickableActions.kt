package dev.veryniche.stitchcounter.tiles.counter

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ModifiersBuilders
import dev.veryniche.stitchcounter.wear.presentation.MainActivity

internal fun launchActivityClickable(
    clickableId: String,
    androidActivity: ActionBuilders.AndroidActivity
) = ModifiersBuilders.Clickable.Builder()
    .setId(clickableId)
    .setOnClick(
        ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(androidActivity)
            .build()
    )
    .build()

internal fun openSelectCounter() = ActionBuilders.AndroidActivity.Builder()
    .setAppActivity()
    .addKeyToExtraMapping(
        MainActivity.EXTRA_JOURNEY,
        ActionBuilders.stringExtra(MainActivity.EXTRA_JOURNEY_SELECT_COUNTER)
    )
    .build()

internal fun ActionBuilders.AndroidActivity.Builder.setAppActivity(): ActionBuilders.AndroidActivity.Builder {
    return setPackageName("dev.veryniche.stitchcounter")
        .setClassName("dev.veryniche.stitchcounter.wear.presentation.MainActivity")
}
