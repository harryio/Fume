package io.github.sainiharry.fume.repository

import androidx.lifecycle.LiveData
import androidx.room.*

internal const val DATABASE_NAME = "FumeDatabase"

@Entity
internal data class AqiDataEntity(
    @PrimaryKey
    val timestamp: Long,
    val vocAqi: Int,
    val no2Aqi: Int,
    val pm25Aqi: Int,
    val pm10Aqi: Int
)

@Dao
internal interface AqiDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(aqiData: AqiDataEntity)

    @Query("SELECT timestamp, MAX(vocAqi, no2Aqi, pm25Aqi, pm10Aqi) AS aqi, vocAqi, no2Aqi, pm25Aqi, pm10Aqi FROM AqiDataEntity ORDER BY timestamp ASC")
    fun getAqiData(): LiveData<List<AqiData>>

    @Query("SELECT * FROM AqiDataEntity ORDER BY timestamp ASC LIMIT 1")
    suspend fun getLatestData(): AqiDataEntity?
}

@Database(entities = [AqiDataEntity::class], version = 1)
internal abstract class AqiDatabase : RoomDatabase() {

    abstract fun aqiDao(): AqiDao
}