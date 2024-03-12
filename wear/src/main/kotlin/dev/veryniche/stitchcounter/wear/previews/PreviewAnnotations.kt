package dev.veryniche.stitchcounter.wear.previews

import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.tooling.preview.devices.WearDevices

@Preview(name = "small font", group = "Small Font", device = WearDevices.SMALL_ROUND, fontScale = 0.85f)
@Preview(name = "regular font", group = "Regular Font", device = WearDevices.SMALL_ROUND, fontScale = 1.0f)
@Preview(name = "large font", group = "Large Font", device = WearDevices.SMALL_ROUND, fontScale = 1.15f)
@Preview(name = "extra large font", group = "Extra Large Font", device = WearDevices.SMALL_ROUND, fontScale = 1.3f)
annotation class PreviewComponent

@Preview(name = "Small round - Small Font", group = "Small Font", device = WearDevices.SMALL_ROUND, fontScale = 0.85f, showSystemUi = true)
@Preview(name = "Small round - Regular Font", group = "Regular Font", device = WearDevices.SMALL_ROUND, fontScale = 1.0f, showSystemUi = true)
@Preview(name = "Small round - Large Font", group = "Large Font", device = WearDevices.SMALL_ROUND, fontScale = 1.15f, showSystemUi = true)
@Preview(name = "Small round - XLarge Font", group = "Extra Large Font", device = WearDevices.SMALL_ROUND, fontScale = 1.3f, showSystemUi = true)
@Preview(name = "Large round", device = WearDevices.LARGE_ROUND, group = "Large Round", showSystemUi = true)
@Preview(name = "Square", device = WearDevices.SQUARE, group = "Square", showSystemUi = true)
@Preview(name = "Rectangle", device = WearDevices.RECT, group = "Rectangle", showSystemUi = true)
annotation class PreviewScreen
