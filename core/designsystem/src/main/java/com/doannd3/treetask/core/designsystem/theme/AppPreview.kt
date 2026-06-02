package com.doannd3.treetask.core.designsystem.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light", showBackground = true)
annotation class AppPreviewLight

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
annotation class AppPreviewLightDark

@Preview(name = "EN", locale = "en")
@Preview(name = "VI", locale = "vi")
annotation class PreviewLocales

@Preview(name = "EN Light", locale = "en", showBackground = true)
@Preview(
    name = "EN Dark",
    locale = "en",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Preview(name = "VI Light", locale = "vi", showBackground = true)
@Preview(
    name = "VI Dark",
    locale = "vi",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
annotation class AppPreviewLocalesLightDark
