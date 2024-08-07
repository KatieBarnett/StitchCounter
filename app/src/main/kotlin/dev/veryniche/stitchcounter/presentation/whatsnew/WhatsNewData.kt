package dev.veryniche.stitchcounter.presentation.whatsnew

import androidx.compose.ui.text.AnnotatedString

data class WhatsNewData(
    val id: Int,
    val text: List<AnnotatedString>
)

val whatsNewData = listOf(
    WhatsNewData(
        id = 1,
        text = listOf(
            AnnotatedString(
                "Using Stitch Counter is even easier! Now with a tile so you can " +
                    "always see your progress and no need to open your favourite counter each time. " +
                    "Just add the tile by swiping your watch face and long pressing."
            ),
            AnnotatedString(
                "Always on screen setting for counter and project screens so your " +
                    "counter does not disappear while you are using it (note - not all wear devices" +
                    " support this setting)."
            )
        )
    ),
    WhatsNewData(
        id = 2,
        text = listOf(
            AnnotatedString(
                "Second"
            ),
            AnnotatedString(
                "Secondb"
            )
        )
    )
)
