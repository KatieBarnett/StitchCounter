package dev.veryniche.stitchcounter.mobile.previews

import android.content.res.Configuration
import android.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Regular Font", group = "Regular Font", device = Devices.PHONE, uiMode = Configuration.UI_MODE_NIGHT_NO, fontScale = 1.0f, backgroundColor = Color.BLACK.toLong())
@Preview(name = "Regular Font", group = "Regular Font", device = Devices.PHONE, uiMode = Configuration.UI_MODE_NIGHT_YES, fontScale = 1.0f, backgroundColor = Color.WHITE.toLong())
annotation class PreviewComponent
//@Preview(name = "Small Font", group = "Small Font", device = Devices.PHONE, fontScale = 0.85f)
//@Preview(name = "Large Font", group = "Large Font", device = Devices.PHONE, fontScale = 1.15f)
//@Preview(name = "XLarge Font", group = "Extra Large Font", device = Devices.PHONE, fontScale = 1.3f)


@Preview(
    name = "Phone - Regular Font",
    group = "Regular Font",
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    fontScale = 1.0f,
    showSystemUi = true,
    backgroundColor = Color.BLACK.toLong(),
)
@Preview(
    name = "Phone - Regular Font",
    group = "Regular Font",
    device = Devices.PIXEL_7,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    fontScale = 1.0f,
    showSystemUi = true,
    backgroundColor = Color.BLACK.toLong(),
)
annotation class PreviewScreen
//@Preview(
//    name = "Phone - Small Font",
//    group = "Small Font",
//    device = Devices.PHONE,
//    fontScale = 0.85f,
//    showSystemUi = true
//)
//@Preview(
//    name = "Phone - Large Font",
//    group = "Large Font",
//    device = Devices.PHONE,
//    fontScale = 1.15f,
//    showSystemUi = true
//)
//@Preview(
//    name = "Phone - XLarge Font",
//    group = "Extra Large Font",
//    device = Devices.PHONE,
//    fontScale = 1.3f,
//    showSystemUi = true
//)
// @Preview(name = "Large round", device = Devices., group = "Large Round", showSystemUi = true)
// @Preview(name = "Square", device = WearDevices.SQUARE, group = "Square", showSystemUi = true)
// @Preview(name = "Rectangle", device = WearDevices.RECT, group = "Rectangle", showSystemUi = true)

