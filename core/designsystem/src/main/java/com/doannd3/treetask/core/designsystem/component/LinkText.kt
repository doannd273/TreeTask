package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

enum class LinkTag {
    REGISTER,
    FORGOT_PASSWORD,
}

data class LinkPart(
    val text: String,
    val tag: String,
    val onClick: () -> Unit,
)

@Composable
fun LinkText(
    text: String,
    links: List<LinkPart>,
    modifier: Modifier = Modifier,
    parentStyle: TextStyle = TextStyle.Default,
    linkStyle: SpanStyle = SpanStyle(
        textDecoration = TextDecoration.Underline,
    ),
) {
    val updatedLinks = links.map {
        val updatedOnClick by rememberUpdatedState(it.onClick)
        it.copy(onClick = { updatedOnClick() })
    }

    val annotatedText = remember(text, updatedLinks, linkStyle) {
        buildAnnotatedString {
            append(text)

            var currentIndex = 0

            updatedLinks.forEach { link ->
                val start = text.indexOf(link.text, currentIndex)
                if (start == -1) return@forEach

                val end = start + link.text.length
                currentIndex = end

                addLink(
                    LinkAnnotation.Clickable(
                        tag = link.tag,
                        linkInteractionListener = { link.onClick() },
                    ),
                    start = start,
                    end = end,
                )

                addStyle(
                    style = linkStyle,
                    start = start,
                    end = end,
                )
            }
        }
    }

    BasicText(
        modifier = modifier,
        text = annotatedText,
        style = parentStyle,
    )
}

@Preview(showBackground = true)
@Composable
private fun LinkTextRegisterPreview() {
    TreeTaskTheme {
        LinkText(
            modifier = Modifier.padding(16.dp),
            text = "Don't have an account? Register",
            links =
            listOf(
                LinkPart(
                    text = "Register",
                    tag = LinkTag.REGISTER.name,
                    onClick = {},
                ),
            ),
            parentStyle =
            TextStyle(
                color = Purple40,
                fontSize = 15.sp,
            ),
            linkStyle =
            SpanStyle(
                color = Purple40,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}
