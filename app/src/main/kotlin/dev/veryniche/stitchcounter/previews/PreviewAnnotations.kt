package dev.veryniche.stitchcounter.previews

import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "small font", group = "Small Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 0.85f)
@Preview(name = "regular font", group = "Regular Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 1.0f)
@Preview(name = "large font", group = "Large Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 1.15f)
@Preview(name = "extra large font", group = "Extra Large Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 1.3f)
annotation class PreviewComponent

@Preview(name = "small font", group = "Small Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 0.85f, showSystemUi = true)
@Preview(name = "regular font", group = "Regular Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 1.0f, showSystemUi = true)
@Preview(name = "large font", group = "Large Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 1.15f, showSystemUi = true)
@Preview(name = "extra large font", group = "Extra Large Font", device = Devices.WEAR_OS_SMALL_ROUND, fontScale = 1.3f, showSystemUi = true)
@Preview(device = Devices.WEAR_OS_LARGE_ROUND, group = "Large Round", showSystemUi = true)
@Preview(device = Devices.WEAR_OS_SQUARE, group = "Square", showSystemUi = true)
@Preview(device = Devices.WEAR_OS_RECT, group = "Rectangle", showSystemUi = true)
annotation class PreviewScreen
