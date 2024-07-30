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
import CounterTileRenderer.Companion.BUTTON_EXTRA_SMALL_SIZE
import CounterTileRenderer.Companion.BUTTON_SMALL_SIZE
import CounterTileRenderer.Companion.ID_IC_ADD
import CounterTileRenderer.Companion.ID_IC_REMOVE
import CounterTileRenderer.Companion.ID_IC_RESET
import android.content.Context
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.wrap
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.expression.ProtoLayoutExperimental
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.ButtonDefaults
import androidx.wear.protolayout.material.CircularProgressIndicator
import androidx.wear.protolayout.material.ProgressIndicatorColors
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.EdgeContentLayout
import androidx.wear.tiles.tooling.preview.TilePreviewData
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.images.drawableResToImageResource
import com.google.android.horologist.tiles.render.SingleTileLayoutRenderer
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.data.models.Counter
import dev.veryniche.stitchcounter.getCounterProgress
import dev.veryniche.stitchcounter.presentation.theme.stitchCounterColorPalette
import dev.veryniche.stitchcounter.previews.PreviewTile
import dev.veryniche.stitchcounter.tiles.counter.CounterTileState

@OptIn(ExperimentalHorologistApi::class)
class CounterTileRenderer(context: Context) :
    SingleTileLayoutRenderer<CounterTileState, Counter>(context) {

    override fun renderTile(
        state: CounterTileState,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
    ): androidx.wear.protolayout.LayoutElementBuilders.LayoutElement {
        return counterTileLayout(
            context = context,
            deviceParameters = deviceParameters,
            projectName = state.projectName,
            counter = state.counter,
            clickablePositive = ModifiersBuilders.Clickable.Builder().build(),
            clickableNegative = ModifiersBuilders.Clickable.Builder().build(),
//            contactClickableFactory = { contact ->
//                launchActivityClickable(
//                    clickableId = contact.id.toString(),
//                    androidActivity = openConversation(contact)
//                )
//            },
//            searchButtonClickable = launchActivityClickable("search_button", openSearch()),
//            newButtonClickable = launchActivityClickable("new_button", openNewConversation())
        ).build()
    }

    override fun Resources.Builder.produceRequestedResources(
        resourceState: Counter,
        deviceParameters: DeviceParametersBuilders.DeviceParameters,
        resourceIds: List<String>,
    ) {
        addIdToImageMapping(ID_IC_ADD, drawableResToImageResource(R.drawable.ic_add))
        addIdToImageMapping(ID_IC_REMOVE, drawableResToImageResource(R.drawable.ic_remove))
        addIdToImageMapping(ID_IC_RESET, drawableResToImageResource(R.drawable.ic_refresh))
    }

    companion object {
        internal const val ID_IC_ADD = "ic_add"
        internal const val ID_IC_REMOVE = "ic_remove"
        internal const val ID_IC_RESET = "ic_reset"

        internal val BUTTON_SMALL_SIZE = DimensionBuilders.dp(40f)
        internal val BUTTON_EXTRA_SMALL_SIZE = DimensionBuilders.dp(32f)
    }
}

@androidx.annotation.OptIn(ProtoLayoutExperimental::class)
fun counterTileLayout(
    context: Context,
    deviceParameters: DeviceParametersBuilders.DeviceParameters,
    projectName: String,
    counter: Counter,
    clickablePositive: ModifiersBuilders.Clickable,
    clickableNegative: ModifiersBuilders.Clickable,
): EdgeContentLayout.Builder {
    return EdgeContentLayout.Builder(deviceParameters)
//        .setResponsiveContentInsetEnabled(true)
        .setEdgeContent(
            CircularProgressIndicator.Builder()
                .setProgress(counter.getCounterProgress() ?: 0f)
                .setStartAngle(-165f)
                .setEndAngle(165f)
                .setCircularProgressIndicatorColors(
                    ProgressIndicatorColors(
                        argb(stitchCounterColorPalette.primaryVariant.toArgb()),
                        argb(stitchCounterColorPalette.secondaryVariant.toArgb())
                    )
                )
                .build()
        )
        .setPrimaryLabelTextContent(
            LayoutElementBuilders.Column.Builder()
                .setWidth(wrap())
                .setHeight(wrap())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(
                    Text.Builder(context, projectName)
                        .setTypography(Typography.TYPOGRAPHY_CAPTION2)
                        .setOverflow(LayoutElementBuilders.TEXT_OVERFLOW_MARQUEE)
                        .setColor(argb(stitchCounterColorPalette.onSecondary.toArgb()))
                        .build()
                ).addContent(
                    Text.Builder(context, counter.name)
                        .setTypography(Typography.TYPOGRAPHY_CAPTION1)
                        .setOverflow(LayoutElementBuilders.TEXT_OVERFLOW_MARQUEE)
                        .setColor(argb(stitchCounterColorPalette.primaryVariant.toArgb()))
                        .build()
                ).build()
        )
        .setContent(
            LayoutElementBuilders.Column.Builder()
                .setWidth(wrap())
                .setHeight(wrap())
                .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
                .addContent(
                    Text.Builder(context, counter.currentCount.toString())
                        .setTypography(Typography.TYPOGRAPHY_DISPLAY2)
                        .setColor(argb(stitchCounterColorPalette.onPrimary.toArgb()))
                        .setOverflow(LayoutElementBuilders.TEXT_OVERFLOW_MARQUEE)
                        .build()
                )
                .addContent(
                    LayoutElementBuilders.Row.Builder()
                        .setWidth(wrap())
                        .setHeight(wrap())
                        .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                        .addContent(
                            Button.Builder(context, clickableNegative)
                                .setContentDescription("Subtract 1")
                                .setIconContent(ID_IC_REMOVE)
                                .setSize(BUTTON_SMALL_SIZE)
                                .setButtonColors(ButtonDefaults.SECONDARY_COLORS)
                                .build()
                        )
                        .addContent(
                            LayoutElementBuilders.Spacer.Builder()
                                .setWidth(dp(16f))
                                .build()
                        )
                        .addContent(
                            Button.Builder(context, clickablePositive)
                                .setContentDescription("Subtract 1")
                                .setIconContent(ID_IC_ADD)
                                .setSize(BUTTON_SMALL_SIZE)
                                .build()
                        )
                        .build()
                )
                .addContent(
                    LayoutElementBuilders.Spacer.Builder()
                        .setHeight(dp(2f))
                        .build()
                )
                .addContent(
                    LayoutElementBuilders.Row.Builder()
                        .setWidth(wrap())
                        .setHeight(wrap())
                        .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                        .addContent(
                            Button.Builder(context, clickableNegative)
                                .setContentDescription("Reset counter")
                                .setIconContent(ID_IC_RESET)
                                .setSize(BUTTON_EXTRA_SMALL_SIZE)
                                .setButtonColors(ButtonDefaults.SECONDARY_COLORS)
                                .build()
                        )
                        .build()
                )
                .build()

        )
}

// do resources, pass in content decription
// pass in icons
// clickables for buttons
// test on emulator
// reset button
// open app button

private fun previewResources() = ResourceBuilders.Resources.Builder()
    .addIdToImageMapping(ID_IC_ADD, drawableResToImageResource(R.drawable.ic_add))
    .addIdToImageMapping(ID_IC_REMOVE, drawableResToImageResource(R.drawable.ic_remove))
    .addIdToImageMapping(ID_IC_RESET, drawableResToImageResource(R.drawable.ic_refresh))
    .build()

@PreviewTile
fun counterTileLayoutPreview(context: Context): TilePreviewData {
    return TilePreviewData({ previewResources() }) { request ->
        CounterTileRenderer(context).renderTimeline(
            CounterTileState(
                projectName = "project name",
                counter = Counter(
                    id = 3,
                    name = "pattern",
                    currentCount = 40000,
                    maxCount = 50000,
                )
            ),
            request
        )
    }
}
