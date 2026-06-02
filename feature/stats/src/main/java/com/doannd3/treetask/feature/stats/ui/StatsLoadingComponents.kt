package com.doannd3.treetask.feature.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLight
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
internal fun StatsLoadingState(modifier: Modifier = Modifier) {
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item(key = "summary_loading") {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(64.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                    shape = CircleShape,
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(34.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                            strokeWidth = 4.dp,
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        LoadingBlock(
                            modifier = Modifier.fillMaxWidth(0.64f).height(20.dp),
                            color = placeholderColor,
                        )
                        LoadingBlock(
                            modifier = Modifier.fillMaxWidth(0.82f).height(14.dp),
                            color = placeholderColor,
                        )
                    }
                }
            }
        }

        item(key = "status_tile_loading") {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        repeat(2) {
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    LoadingBlock(
                                        modifier = Modifier.size(16.dp),
                                        color = placeholderColor,
                                        shape = CircleShape,
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    LoadingBlock(
                                        modifier = Modifier.fillMaxWidth(0.38f).height(28.dp),
                                        color = placeholderColor,
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    LoadingBlock(
                                        modifier = Modifier.fillMaxWidth(0.68f).height(16.dp),
                                        color = placeholderColor,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item(key = "workload_loading") {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    LoadingBlock(
                        modifier = Modifier.width(160.dp).height(20.dp),
                        color = placeholderColor,
                    )

                    Spacer(Modifier.height(16.dp))

                    LoadingBlock(
                        modifier = Modifier.fillMaxWidth().height(18.dp),
                        color = placeholderColor,
                        shape = RoundedCornerShape(50),
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        repeat(4) {
                            LoadingBlock(
                                modifier = Modifier.width(64.dp).height(14.dp),
                                color = placeholderColor,
                            )
                        }
                    }
                }
            }
        }

        item(key = "recent_tasks_loading") {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(10.dp),
            ) {
                Column {
                    LoadingBlock(
                        modifier = Modifier.padding(16.dp).width(120.dp).height(20.dp),
                        color = placeholderColor,
                    )

                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )

                    repeat(3) { index ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            LoadingBlock(
                                modifier = Modifier.weight(1f).height(16.dp),
                                color = placeholderColor,
                            )
                            LoadingBlock(
                                modifier = Modifier.width(72.dp).height(20.dp),
                                color = placeholderColor,
                                shape = RoundedCornerShape(10.dp),
                            )
                            LoadingBlock(
                                modifier = Modifier.width(48.dp).height(16.dp),
                                color = placeholderColor,
                            )
                        }

                        if (index < 2) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@AppPreviewLight
@Composable
private fun StatsLoadingStatePreview() {
    TreeTaskTheme {
        StatsLoadingState()
    }
}

@Composable
private fun LoadingBlock(
    modifier: Modifier,
    color: Color,
    shape: Shape = RoundedCornerShape(6.dp),
) {
    Box(
        modifier =
            modifier
                .background(
                    color = color,
                    shape = shape,
                ),
    )
}
