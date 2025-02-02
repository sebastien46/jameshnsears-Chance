package com.github.jameshnsears.chance.data.repository.settings.impl

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.jameshnsears.chance.data.BuildConfig
import com.github.jameshnsears.chance.data.domain.core.settings.SettingsDataInterface
import com.github.jameshnsears.chance.data.domain.core.settings.impl.SettingsDataImpl
import com.github.jameshnsears.chance.data.domain.proto.SettingsProtocolBuffer
import com.github.jameshnsears.chance.data.repository.settings.RepositorySettingsInterface
import com.github.jameshnsears.chance.utility.feature.UtilityFeature
import com.google.protobuf.util.JsonFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class RepositorySettingsImpl private constructor(private val context: Context) :
    RepositorySettingsInterface {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: RepositorySettingsImpl? = null

        fun getInstance(
            context: Context,
            settings: SettingsDataInterface = SettingsDataImpl()
        ): RepositorySettingsImpl {
            if (instance == null) {
                synchronized(this) {
                    runBlocking {
                        instance = RepositorySettingsImpl(context)

                        if (BuildConfig.DEBUG) {
                            if (!UtilityFeature.isEnabled(UtilityFeature.Flag.USE_PROTO_REPO)) {
                                instance!!.clear()
                            }
                        }

                        if (instance!!.fetch().first().resize == 0) {
                            instance!!.store(settings)
                        }
                    }
                }
            }

            return instance!!
        }
    }

    override suspend fun jsonExport(): String =
        JsonFormat.printer().includingDefaultValueFields()
            .print(context.settingsDataStore.data.first())

    override suspend fun jsonImport(json: String) {
        store(jsomImportProcess(json))
    }

    override fun jsomImportProcess(json: String): SettingsDataInterface {
        val settingsProtocolBufferBuilder: SettingsProtocolBuffer.Builder =
            SettingsProtocolBuffer.newBuilder()

        JsonFormat.parser().merge(json, settingsProtocolBufferBuilder)

        val settingsProtocolBuffer = settingsProtocolBufferBuilder.build()

        val newSettings = SettingsDataImpl()

        newSettings.resize = settingsProtocolBuffer.resize

        newSettings.rollIndexTime = settingsProtocolBuffer.rollIndexTime
        newSettings.rollScore = settingsProtocolBuffer.rollScore

        newSettings.diceTitle = settingsProtocolBuffer.diceTitle
        newSettings.sideNumber = settingsProtocolBuffer.sideNumber
        newSettings.rollBehaviour = settingsProtocolBuffer.behaviour
        newSettings.sideDescription = settingsProtocolBuffer.sideDescription
        newSettings.sideSVG = settingsProtocolBuffer.sideSVG

        newSettings.rollSound = settingsProtocolBuffer.rollSound

        return newSettings
    }

    override suspend fun fetch(): Flow<SettingsDataImpl> = flow {
        val settings = context.settingsDataStore.data
            .map { settingsProtocolBuffer ->
                SettingsDataImpl(
                    resize = settingsProtocolBuffer.resize,

                    rollIndexTime = settingsProtocolBuffer.rollIndexTime,
                    rollScore = settingsProtocolBuffer.rollScore,

                    diceTitle = settingsProtocolBuffer.diceTitle,
                    sideNumber = settingsProtocolBuffer.sideNumber,
                    rollBehaviour = settingsProtocolBuffer.behaviour,
                    sideDescription = settingsProtocolBuffer.sideDescription,
                    sideSVG = settingsProtocolBuffer.sideSVG,

                    rollSound = settingsProtocolBuffer.rollSound,
                )
            }.first()

        Timber.d("repositorySettings.FETCH ============================================")
        Timber.d("repositorySettings.resize=${settings.resize}")

        emit(settings)
    }

    override suspend fun store(newSettingsData: SettingsDataInterface) {
        Timber.d("repositorySettings.STORE ============================================")
        Timber.d("repositorySettings.resize=${newSettingsData.resize}")

        context.settingsDataStore.updateData {
            val settingsProtocolBufferBuilder = it.toBuilder()
            mapSettingsIntoSettingsProtocolBufferBuilder(
                newSettingsData,
                settingsProtocolBufferBuilder
            )
            settingsProtocolBufferBuilder.build()
        }
    }

    override suspend fun clear() {
        context.settingsDataStore.updateData {
            it.toBuilder().clear().build()
        }
    }
}

val Context.settingsDataStore: DataStore<SettingsProtocolBuffer> by dataStore(
    // /data/data/com.github.jameshnsears.chance.test.test/files/datastore
    fileName = "settings.pb",
    serializer = SettingsProtocolBufferSerializer,
)
