package com.github.jameshnsears.chance.ui.dialog.bag.compose

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import com.github.jameshnsears.chance.data.domain.core.bag.testdouble.BagDataTestDouble
import com.github.jameshnsears.chance.data.repository.RepositoryFactory
import com.github.jameshnsears.chance.data.repository.bag.testdouble.RepositoryBagTestDouble
import com.github.jameshnsears.chance.ui.dialog.bag.DialogBagAndroidViewModel
import com.github.jameshnsears.chance.ui.theme.ChanceTheme
import com.github.jameshnsears.chance.utility.feature.UtilityFeature
import com.github.jameshnsears.chance.utility.feature.UtilityFeature.Flag
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@SuppressLint("UnrememberedMutableState")
@Preview(widthDp = 700, heightDp = 1800)
@Composable
fun DialogBagPreview() {
    UtilityFeature.enabled = setOf(
        Flag.NONE,
    )

    val showDialog = mutableStateOf(true)

    val repositoryBagTestDouble =
        RepositoryBagTestDouble.getInstance(RepositoryFactory().bagDataTestDouble.allDice)
    runBlocking(Dispatchers.Main) {
        repositoryBagTestDouble.store(
            mutableListOf(
                BagDataTestDouble().d2,
            )
        )
    }

    val dice = BagDataTestDouble().d2
    val sides = dice.sides[0]

    val dialogBagAndroidViewModel = runBlocking {
        DialogBagAndroidViewModel(
            mockk<Application>(),
            repositoryBagTestDouble,
            dice,
            sides,
        )
    }

    ChanceTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DialogBagLayout(
                showDialog,
                dialogBagAndroidViewModel,
            )
        }
    }
}
