/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// @file:OptIn(ExperimentalHorologistApi::class)

// import com.google.android.horologist.compose.tools.buildDeviceParameters
//
// import com.example.wear.tiles.messaging.tile.MessagingTileRenderer.Companion.ID_IC_SEARCH
import CounterTileRenderer.Companion.ID_IC_ADD
import CounterTileRenderer.Companion.ID_IC_EDIT
import CounterTileRenderer.Companion.ID_IC_REMOVE
import CounterTileRenderer.Companion.ID_IC_RESET
import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.StateBuilders
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.tiles.counter.CounterTileState
import dev.veryniche.stitchcounter.tiles.counter.launchActivityClickable
import dev.veryniche.stitchcounter.tiles.counter.layouts.counterTileLayout
import dev.veryniche.stitchcounter.tiles.counter.layouts.emptyCounterTileLayout
import dev.veryniche.stitchcounter.tiles.counter.openSelectCounter

@OptIn(ExperimentalHorologistApi::class)
class CounterTileRenderer(context: Context) :
    SingleTileLayoutRenderer<CounterTileState, Counter>(context) {

    override fun renderTile(
        state: CounterTileState,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
    ): LayoutElementBuilders.LayoutElement {
        return if (state.counter != null && state.project != null) {
            counterTileLayout(
                context = context,
                deviceParameters = deviceParameters,
                projectName = state.project.name,
                counter = state.counter,
                clickablePositive = ModifiersBuilders.Clickable.Builder().build(),
                clickableNegative = ModifiersBuilders.Clickable.Builder().build(),
                clickableReset = ModifiersBuilders.Clickable.Builder()
                    .setId("reset_counter")
                    .setOnClick(
                        ActionBuilders.LoadAction.Builder()
                            .setRequestState(
                                StateBuilders.State.Builder()
                                    .build()
                            ).build()
                    ).build(),
                clickableEdit = launchActivityClickable("select_counter", openSelectCounter()),
            ).build()
        } else {
            emptyCounterTileLayout(
                context = context,
                deviceParameters = deviceParameters,
                openApp = launchActivityClickable("select_counter", openSelectCounter()),
            ).build()
        }
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceState: Counter,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: List<String>,
    ) {
        addIdToImageMapping(ID_IC_ADD, drawableResToImageResource(R.drawable.ic_add))
        addIdToImageMapping(ID_IC_REMOVE, drawableResToImageResource(R.drawable.ic_remove))
        addIdToImageMapping(ID_IC_RESET, drawableResToImageResource(R.drawable.ic_refresh))
        addIdToImageMapping(ID_IC_EDIT, drawableResToImageResource(R.drawable.ic_edit))
    }

    companion object {
        internal const val ID_IC_ADD = "ic_add"
        internal const val ID_IC_REMOVE = "ic_remove"
        internal const val ID_IC_RESET = "ic_reset"
        internal const val ID_IC_EDIT = "ic_edit"

        internal val BUTTON_SMALL_SIZE = DimensionBuilders.dp(40f)
        internal val BUTTON_EXTRA_SMALL_SIZE = DimensionBuilders.dp(32f)
    }
}

fun previewResources() = Resources.Builder()
    .addIdToImageMapping(ID_IC_ADD, drawableResToImageResource(R.drawable.ic_add))
    .addIdToImageMapping(ID_IC_REMOVE, drawableResToImageResource(R.drawable.ic_remove))
    .addIdToImageMapping(ID_IC_RESET, drawableResToImageResource(R.drawable.ic_refresh))
    .addIdToImageMapping(ID_IC_EDIT, drawableResToImageResource(R.drawable.ic_edit))
    .build()
