package com.maunc.randomcallname.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maunc.randomcallname.RandomNameApplication
import com.maunc.randomcallname.constant.DATA_BASE_NAME
import com.maunc.randomcallname.database.dao.RandomNameDao
import com.maunc.randomcallname.database.dao.RandomNameGroupDao
import com.maunc.randomcallname.database.dao.RandomNameTransactionDao
import com.maunc.randomcallname.database.table.RandomNameData
import com.maunc.randomcallname.database.table.RandomNameGroup

val randomNameDataBase: RandomNameDataBase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    RandomNameDataBase.DATABASE_INSTANCE
}

val randomNameDao: RandomNameDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    randomNameDataBase.obtainRandomNameDao()
}

val randomGroupDao: RandomNameGroupDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    randomNameDataBase.obtainRandomNameGroupDao()
}

val randomNameTransactionDao: RandomNameTransactionDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    randomNameDataBase.obtainRandomNameTransactionDao()
}

@Database(
    entities = [RandomNameData::class, RandomNameGroup::class],
    version = 1,
    exportSchema = false
)
abstract class RandomNameDataBase : RoomDatabase() {

    companion object {
        val DATABASE_INSTANCE: RandomNameDataBase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                RandomNameApplication.app, RandomNameDataBase::class.java, DATA_BASE_NAME
            ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
        }
    }

    abstract fun obtainRandomNameDao(): RandomNameDao

    abstract fun obtainRandomNameGroupDao(): RandomNameGroupDao

    abstract fun obtainRandomNameTransactionDao(): RandomNameTransactionDao
}