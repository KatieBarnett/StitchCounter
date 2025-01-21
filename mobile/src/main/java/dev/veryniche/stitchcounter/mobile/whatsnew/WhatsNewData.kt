package dev.veryniche.stitchcounter.mobile.whatsnew

import androidx.compose.ui.text.AnnotatedString

data class WhatsNewData(
    val id: Int,
    val text: List<AnnotatedString>
)

val whatsNewData = listOf(
    WhatsNewData(
        id = 2,
        text = listOf(
            AnnotatedString(
                "Counters with a max value will now automatically reset back to one once the max value as been passed."
            )
        )
    ),
)
