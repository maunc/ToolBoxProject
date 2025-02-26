package com.maunc.randomcallname.viewmodel

import com.maunc.randomcallname.base.BaseModel
import com.maunc.randomcallname.base.BaseViewModel
import com.maunc.randomcallname.database.randomNameDataBase
import com.maunc.randomcallname.database.table.RandomNameData
import com.maunc.randomcallname.database.table.RandomNameGroup
import com.maunc.randomcallname.ext.launch
import com.maunc.randomcallname.ext.loge

class WelcomeViewModel : BaseViewModel<BaseModel>() {

    fun testNameDataBase1() {
        launch({
            randomNameDataBase.obtainRandomNameDao()
                .insertRandomName(RandomNameData("第一组", "柯南"))
        }, { "第一组添 柯南 加成功".loge() }, {})
    }

    fun testNameDataBase2() {
        launch({
            randomNameDataBase.obtainRandomNameDao()
                .insertRandomName(RandomNameData("第一组", "索隆"))
        }, { "第一组添 索隆 加成功".loge() }, {})
    }

    fun testGroupDataBase() {
        launch({
            randomNameDataBase.obtainRandomNameGroupDao()
                .insertRandomNameGroup(RandomNameGroup("第一组"))
        }, {
            "第一组添加成功".loge()
        }, {
            "第一组添加失败".loge()
        })
    }

    fun aa() {
        launch({
            randomNameDataBase.obtainRandomGroupWithNameDao().queryRandomNameWithGroup()
        }, {
            it.forEach { data ->
                "${data.randomNameGroup}".loge()
                data.randomNameDataList.forEach { name ->
                    "${name.toGroupName} ${name.randomName}".loge()
                }
            }
        }, {})
    }

    fun bb() {
        launch({
            randomNameDataBase.obtainRandomNameDao().queryGroupToRandomNameSize("第一组")
        }, {
            "$it  个数".loge()
        }, {})
    }
}