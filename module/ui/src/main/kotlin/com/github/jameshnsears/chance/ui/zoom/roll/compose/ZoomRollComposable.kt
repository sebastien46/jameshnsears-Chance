package com.github.jameshnsears.chance.ui.zoom.roll.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.jameshnsears.chance.common.R
import com.github.jameshnsears.chance.data.domain.core.Dice
import com.github.jameshnsears.chance.data.domain.core.Side
import com.github.jameshnsears.chance.data.domain.core.roll.Roll
import com.github.jameshnsears.chance.ui.tab.roll.TabRollAndroidViewModel
import com.github.jameshnsears.chance.ui.zoom.ZoomAndroidViewModel
import com.github.jameshnsears.chance.ui.zoom.compose.ZoomSideDescription
import com.github.jameshnsears.chance.utility.feature.UtilityFeature
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ZoomRoll(
    tabRollAndroidViewModel: TabRollAndroidViewModel,
    zoomAndroidViewModel: ZoomAndroidViewModel,
) {
    val stateFlowZoom =
        zoomAndroidViewModel.stateFlowZoom.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )

    val rollHistory = stateFlowZoom.value.rollHistory

    val stateFlowTabRoll =
        tabRollAndroidViewModel.stateFlowSettings.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )

    val listState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.padding(top = 8.dp, start = 0.dp, bottom = 110.dp, end = 8.dp),
        state = listState,
    ) {
        itemsIndexed(
            items = stateFlowZoom.value.rollHistory.entries.toList(),
            key = { index, item -> "${item.value}_${index}" }
        ) { index, rollSequence ->
            if (stateFlowTabRoll.value.rollIndexTime)
                RollIndexTime(
                    rollHistory.size - index,
                    rollSequence
                )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (stateFlowTabRoll.value.rollScore)
                    RollScore(tabRollAndroidViewModel, rollSequence)

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    itemsIndexed(
                        rollSequence.value,
                        key = { index, item ->
                            "${item.side}_${index}"
                        }
                    ) { _, roll ->
                        RollDetails(
                            tabRollAndroidViewModel,
                            zoomAndroidViewModel,
                            roll,
                            zoomAndroidViewModel.fetchDiceFromEpochCache(roll.diceEpoch)
                        )
                    }
                }
            }

            if (index < rollHistory.entries.size
                &&
                tabRollAndroidViewModel.isContentAvailableToDisplay(rollSequence.value)
            )
                if (index == 0)
                    HorizontalDivider(
                        Modifier.padding(bottom = 12.dp),
                        thickness = 4.dp
                    )
                else
                    HorizontalDivider(Modifier.padding(bottom = 12.dp))
        }
    }
}

@Composable
private fun RollIndexTime(
    position: Int,
    rollHistory: MutableMap.MutableEntry<Long, List<Roll>>
) {
    Row(
        modifier = Modifier.padding(start = 8.dp),
    ) {
        Text(
            text = "$position : ${
                SimpleDateFormat("dd, MMMM HH:mm:ss", Locale.getDefault())
                    .format(Date(rollHistory.key))
            }"
        )
    }
}

@Composable
private fun RollScore(
    tabRollAndroidViewModel: TabRollAndroidViewModel,
    rollSequence: MutableMap.MutableEntry<Long, List<Roll>>
) {
    Column(
        Modifier
            .padding(start = 8.dp, end = 12.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = tabRollAndroidViewModel.diceSequenceScore(rollSequence),
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RollDetails(
    tabRollAndroidViewModel: TabRollAndroidViewModel,
    zoomAndroidViewModel: ZoomAndroidViewModel,
    roll: Roll,
    dice: Dice
) {
    val stateFlowTabRoll =
        tabRollAndroidViewModel.stateFlowSettings.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )

    val settingsDiceTitle = stateFlowTabRoll.value.diceTitle

    val settingsSideNumber = stateFlowTabRoll.value.sideNumber

    val settingsBehaviour = stateFlowTabRoll.value.rollBehaviour

    val settingsSideDescription = stateFlowTabRoll.value.sideDescription

    val settingsSideSVG = stateFlowTabRoll.value.sideSVG

    Column(
        Modifier.padding(start = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (settingsDiceTitle)
            Row(Modifier.padding(4.dp)) {
                if (UtilityFeature.isEnabled(UtilityFeature.Flag.SHOW_EPOCH_UUID)) {
                    Column {
                        Text(dice.title)
                        Text("${dice.epoch}")
                        Text(dice.uuid)
                        Text(roll.side.uuid)
                    }
                } else {
                    Text(dice.title)
                }
            }

        if (settingsBehaviour) ZoomSideRollBehaviour(zoomAndroidViewModel, roll)

        if (settingsSideNumber) ZoomRollSideImageShape(
            zoomAndroidViewModel,
            dice,
            roll.side
        )

        if (settingsSideDescription) ZoomSideDescription(zoomAndroidViewModel, dice, roll.side)

        if (settingsSideSVG) ZoomRollSideImageSVG(
            zoomAndroidViewModel,
            roll.side
        )
    }
}

@Composable
fun ZoomSideRollBehaviour(
    zoomAndroidViewModel: ZoomAndroidViewModel,
    roll: Roll
) {
    val rollSelectionIconColour = if (isSystemInDarkTheme()) Color.White else Color.Black

    val stateFlowZoom =
        zoomAndroidViewModel.stateFlowZoom.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )

    val resizeView = stateFlowZoom.value.resizeView

    Box {
        Row(
            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ZoomSideRollBehaviourIcon(
                resizeView,
                rollSelectionIconColour,
                zoomAndroidViewModel,
                R.drawable.roll_repeat_fill0_wght400_grad0_opsz24,
                "${roll.multiplierIndex}"
            )

            if (roll.explodeIndex != 0) {
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                ZoomSideRollBehaviourIcon(
                    resizeView,
                    rollSelectionIconColour,
                    zoomAndroidViewModel,
                    R.drawable.roll_explode_fill0_wght400_grad0_opsz24,
                    "${roll.explodeIndex}"
                )
            }

            if (roll.scoreAdjustment != 0) {
                ZoomSideRollBehaviourIcon(
                    resizeView,
                    rollSelectionIconColour,
                    zoomAndroidViewModel,
                    R.drawable.roll_add_subtract_fill0_wght400_grad0_opsz24,
                    "${roll.scoreAdjustment}"
                )
            }
        }
    }
}

@Composable
private fun ZoomSideRollBehaviourIcon(
    resizeView: Dp,
    rollSelectionIconColour: Color,
    zoomAndroidViewModel: ZoomAndroidViewModel,
    drawableId: Int,
    text: String,
) {
    Icon(
        painterResource(id = drawableId),
        contentDescription = "",
        modifier = Modifier.size(resizeView / 4),
        tint = rollSelectionIconColour
    )

    Text(
        fontSize = zoomAndroidViewModel.sideImageShapeNumberFontSize(),
        text = text,
        color = rollSelectionIconColour,
    )
}

@Composable
fun ZoomRollSideImageShape(
    zoomAndroidViewModel: ZoomAndroidViewModel,
    dice: Dice,
    side: Side
) {
    val stateFlowZoom =
        zoomAndroidViewModel.stateFlowZoom.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )

    val resizeView = stateFlowZoom.value.resizeView

    Box {
        Image(
            painter = painterResource(zoomAndroidViewModel.sideImageShapeNumberShape(dice)),
            contentDescription = "",
            modifier = Modifier
                .size(resizeView)
                .padding(top = 8.dp),
            colorFilter = zoomAndroidViewModel.sideColourFilter(dice.colour),
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 0.dp),
            fontSize = zoomAndroidViewModel.sideImageShapeNumberFontSize(),
            text = "${side.number}",
            color = zoomAndroidViewModel.sideColor(side.numberColour),
        )
    }
}

@Composable
fun ZoomRollSideImageSVG(
    zoomAndroidViewModel: ZoomAndroidViewModel,
    side: Side
) {
    val stateFlowZoom =
        zoomAndroidViewModel.stateFlowZoom.collectAsStateWithLifecycle(
            lifecycleOwner = LocalLifecycleOwner.current
        )

    val resizeView = stateFlowZoom.value.resizeView

    val modifier = Modifier
        .size(resizeView)
        .padding(top = 8.dp)

    if (side.imageDrawableId != 0) {
        Image(
            painter = painterResource(id = side.imageDrawableId),
            contentDescription = "",
            modifier = modifier
        )
    } else {
        if (side.imageBase64 != "") {
            val imageRequest: ImageRequest = remember {
                zoomAndroidViewModel.sideImageSVG(side)
            }

            AsyncImage(
                model = imageRequest,
                contentDescription = "",
                modifier = modifier
            )
        }
    }
}