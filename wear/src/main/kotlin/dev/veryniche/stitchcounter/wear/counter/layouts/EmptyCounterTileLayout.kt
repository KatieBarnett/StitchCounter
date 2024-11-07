package dev.veryniche.stitchcounter.wear.counter.layouts

import CounterTileRenderer
import android.content.Context
import androidx.annotation.OptIn
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.expression.ProtoLayoutExperimental
import androidx.wear.protolayout.material.ChipColors
import androidx.wear.protolayout.material.CompactChip
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.tooling.preview.TilePreviewData
import dev.veryniche.stitchcounter.R
import dev.veryniche.stitchcounter.previews.PreviewTile
import dev.veryniche.stitchcounter.tiles.counter.CounterTileState
import dev.veryniche.stitchcounter.tiles.counter.StitchCounterTileTheme
import previewResources

@OptIn(ProtoLayoutExperimental::class)
fun emptyCounterTileLayout(
    context: Context,
    deviceParameters: DeviceParametersBuilders.DeviceParameters,
    openApp: ModifiersBuilders.Clickable,
) = PrimaryLayout.Builder(deviceParameters)
    .setResponsiveContentInsetEnabled(true)
    .setPrimaryLabelTextContent(
        Text.Builder(context, context.getString(R.string.counter_tile_label))
            .setTypography(Typography.TYPOGRAPHY_TITLE3)
            .setOverflow(LayoutElementBuilders.TEXT_OVERFLOW_MARQUEE)
            .setColor(argb(StitchCounterTileTheme.colors.primary))
            .build()
    )
    .setContent(
        Text.Builder(context, context.getString(R.string.counter_tile_no_counter))
            .setTypography(Typography.TYPOGRAPHY_BODY1)
            .setMaxLines(3)
            .setColor(argb(StitchCounterTileTheme.colors.onPrimary))
            .build()
    )
    .setPrimaryChipContent(
        CompactChip.Builder(
            context,
            context.getString(R.string.counter_tile_open_app),
            /* clickable = */
            openApp,
            /* deviceParameters = */
            deviceParameters
        )
            .setChipColors(ChipColors.primaryChipColors(StitchCounterTileTheme.colors))
            .build()
    ).build()

@PreviewTile
fun emptyCounterTileLayoutPreview(context: Context): TilePreviewData {
    return TilePreviewData({ previewResources() }) { request ->
        CounterTileRenderer(context).renderTimeline(
            CounterTileState(
                counter = null,
                project = null
            ),
            request
        )
    }
}
