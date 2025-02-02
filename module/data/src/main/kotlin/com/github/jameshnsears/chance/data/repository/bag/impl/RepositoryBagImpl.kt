package com.github.jameshnsears.chance.data.repository.bag.impl

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.jameshnsears.chance.data.BuildConfig
import com.github.jameshnsears.chance.data.domain.core.Dice
import com.github.jameshnsears.chance.data.domain.core.bag.DiceBag
import com.github.jameshnsears.chance.data.domain.proto.BagProtocolBuffer
import com.github.jameshnsears.chance.data.repository.bag.RepositoryBagInterface
import com.github.jameshnsears.chance.utility.feature.UtilityFeature
import com.google.protobuf.util.JsonFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class RepositoryBagImpl private constructor(private val context: Context) :
    RepositoryBagInterface {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: RepositoryBagImpl? = null

        fun getInstance(
            context: Context,
            diceBag: DiceBag
        ): RepositoryBagImpl {
            if (instance == null) {
                synchronized(this) {
                    instance = RepositoryBagImpl(context)

                    runBlocking {
                        if (BuildConfig.DEBUG) {
                            if (!UtilityFeature.isEnabled(UtilityFeature.Flag.USE_PROTO_REPO)) {
                                instance!!.clear()
                            }
                        }

                        if (instance!!.fetch().first().size == 0)
                            instance!!.store(diceBag)

                        instance!!.traceUuid(instance!!.fetch().first())
                    }
                }
            }

            return instance!!
        }
    }

    override suspend fun jsonExport(): String =
        JsonFormat.printer().includingDefaultValueFields()
            .print(context.diceBagDataStore.data.first())

    override suspend fun jsonImport(json: String) {
        store(jsomImportProcess(json))
    }

    override suspend fun fetch(): Flow<DiceBag> = flow {
        val diceBag = mutableListOf<Dice>()

        context.diceBagDataStore.data
            .map { bagProtocolBuffer ->
                bagProtocolBuffer.diceList.forEach { diceProtocolBuffer ->
                    diceBag.add(
                        Dice(
                            epoch = diceProtocolBuffer.epoch,

                            sides = jsomImportProcessSides(diceProtocolBuffer),

                            title = diceProtocolBuffer.title,
                            colour = diceProtocolBuffer.colour,
                            selected = diceProtocolBuffer.selected,

                            multiplierValue = diceProtocolBuffer.multiplierValue,

                            explode = diceProtocolBuffer.explode,
                            explodeWhen = diceProtocolBuffer.explodeWhen,
                            explodeValue = diceProtocolBuffer.explodeValue,

                            modifyScore = diceProtocolBuffer.modifyScore,
                            modifyScoreValue = diceProtocolBuffer.modifyScoreValue
                        )
                    )
                }
            }.first()

        Timber.d("repositoryBag.FETCH ============================================")
        Timber.d("repositoryBag.size=${diceBag.size}")

        emit(diceBag)
    }

    override suspend fun fetch(epoch: Long): Flow<Dice> = flow {
        val dice = Dice()

        context.diceBagDataStore.data
            .map { bagProtocolBuffer ->
                bagProtocolBuffer.diceList.forEach { diceProtocolBuffer ->

                    if (epoch == diceProtocolBuffer.epoch) {
                        dice.epoch = diceProtocolBuffer.epoch

                        dice.sides = jsomImportProcessSides(diceProtocolBuffer)

                        dice.title = diceProtocolBuffer.title
                        dice.colour = diceProtocolBuffer.colour
                        dice.selected = diceProtocolBuffer.selected

                        dice.multiplierValue = diceProtocolBuffer.multiplierValue

                        dice.explode = diceProtocolBuffer.explode
                        dice.explodeWhen = diceProtocolBuffer.explodeWhen
                        dice.explodeValue = diceProtocolBuffer.explodeValue

                        dice.modifyScore = diceProtocolBuffer.modifyScore
                        dice.modifyScoreValue = diceProtocolBuffer.modifyScoreValue
                    }
                }
            }.first()

        emit(dice)
    }

    override suspend fun store(newDiceBag: DiceBag) {
        clear()

        Timber.d("repositoryBag.STORE ============================================")
        Timber.d("repositoryBag.size=${newDiceBag.size}")

        context.diceBagDataStore.updateData {
            val bagProtocolBufferBuilder = it.toBuilder()
            mapDiceBagIntoBagProtocolBufferBuilder(
                newDiceBag,
                bagProtocolBufferBuilder
            )
            bagProtocolBufferBuilder.build()
        }
    }

    override suspend fun clear() {
        context.diceBagDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }
}

val Context.diceBagDataStore: DataStore<BagProtocolBuffer> by dataStore(
    // /data/data/com.github.jameshnsears.chance.test.test/files/datastore
    fileName = "bag.pb",
    serializer = BagProtocolBufferSerializer,
)