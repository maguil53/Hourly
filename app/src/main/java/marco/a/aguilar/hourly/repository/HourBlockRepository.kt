package marco.a.aguilar.hourly.repository

import android.content.Context
import androidx.lifecycle.LiveData
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.persistence.AppDatabase

class HourBlockRepository private constructor(context: Context) {

    private var database: AppDatabase = AppDatabase.getInstance(context)
    private var hourBlocks: LiveData<List<HourBlock>> = database.hourBlockDao().getHourBlocks()

    companion object {
        private var instance: HourBlockRepository? = null

        fun getInstance(context: Context): HourBlockRepository {
            return instance ?: HourBlockRepository(context)
        }
    }

    fun getHourBlocks(): LiveData<List<HourBlock>> {
        return hourBlocks
    }
}