package com.maunc.toolbox.commonbase.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maunc.base.BaseApp
import com.maunc.toolbox.commonbase.constant.DATA_BASE_NAME
import com.maunc.toolbox.commonbase.data.ToolBoxItemDao
import com.maunc.toolbox.commonbase.data.ToolBoxItemData
import com.maunc.toolbox.randomname.database.dao.RandomNameDao
import com.maunc.toolbox.randomname.database.dao.RandomNameGroupDao
import com.maunc.toolbox.randomname.database.dao.RandomNameTransactionDao
import com.maunc.toolbox.randomname.database.table.RandomNameData
import com.maunc.toolbox.randomname.database.table.RandomNameGroup
import com.maunc.toolbox.turntable.database.dao.TurnTableDataDao
import com.maunc.toolbox.turntable.database.table.TurnTableGroupData
import com.maunc.toolbox.turntable.database.table.TurnTableNameData

val toolBoxProjectDataBase: ToolBoxProjectDataBase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    ToolBoxProjectDataBase.DATABASE_INSTANCE
}

/**首页*/
val toolBoxItemDao: ToolBoxItemDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    toolBoxProjectDataBase.obtainToolBoxDao()
}

/**点名*/
val randomNameDao: RandomNameDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    toolBoxProjectDataBase.obtainRandomNameDao()
}

val randomGroupDao: RandomNameGroupDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    toolBoxProjectDataBase.obtainRandomNameGroupDao()
}

val randomNameTransactionDao: RandomNameTransactionDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    toolBoxProjectDataBase.obtainRandomNameTransactionDao()
}

/**转盘*/
val turnTableDataDao: TurnTableDataDao by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    toolBoxProjectDataBase.obtainTurnTableDataDao()
}

@Database(
    entities = [
        ToolBoxItemData::class,
        RandomNameData::class,
        RandomNameGroup::class,
        TurnTableNameData::class,
        TurnTableGroupData::class
    ], version = 1, exportSchema = false
)
abstract class ToolBoxProjectDataBase : RoomDatabase() {

    companion object {
        val DATABASE_INSTANCE: ToolBoxProjectDataBase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                BaseApp.app, ToolBoxProjectDataBase::class.java, DATA_BASE_NAME
            ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
        }
    }

    /**首页列表*/
    abstract fun obtainToolBoxDao(): ToolBoxItemDao

    /** 随机姓名表 */
    abstract fun obtainRandomNameDao(): RandomNameDao

    abstract fun obtainRandomNameGroupDao(): RandomNameGroupDao

    abstract fun obtainRandomNameTransactionDao(): RandomNameTransactionDao

    /** 转盘数据表 */
    abstract fun obtainTurnTableDataDao(): TurnTableDataDao
}