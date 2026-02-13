package com.maunc.toolbox.turntable.viewmodel

import androidx.lifecycle.MutableLiveData
import com.maunc.toolbox.commonbase.base.BaseModel
import com.maunc.toolbox.commonbase.base.BaseViewModel
import com.maunc.toolbox.commonbase.database.turnTableDataDao
import com.maunc.toolbox.commonbase.ext.launch
import com.maunc.toolbox.commonbase.ext.loge
import com.maunc.toolbox.commonbase.ext.mutableListInsert
import com.maunc.toolbox.turntable.constant.editDataToStringList
import com.maunc.toolbox.turntable.database.table.TurnTableGroupData
import com.maunc.toolbox.turntable.database.table.TurnTableNameData
import com.maunc.toolbox.turntable.database.table.TurnTableNameWithGroup

class TurnTableBuiltinDataViewModel : BaseViewModel<BaseModel>() {

    var builtinDataLiveData = MutableLiveData<MutableList<TurnTableNameWithGroup>>(mutableListOf())

    private var builtinDataOneTitle = "今天早上吃什么？"
    private var builtinDataTwoTitle = "国庆去哪玩？"
    private var builtinDataThreeTitle = "如何才能原谅老公"
    private var builtinDataFourTitle = "选择真心话或大冒险"

    fun initBuiltinData() {
        builtinDataLiveData.value = mutableListOf<TurnTableNameWithGroup>().apply {
            mutableListInsert(
                TurnTableNameWithGroup(
                    turnTableGroupData = TurnTableGroupData(groupName = builtinDataOneTitle),
                    turnTableNameDataList = mutableListOf<TurnTableNameData>().apply {
                        mutableListInsert(
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "豆腐脑"),
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "胡辣汤"),
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "豆沫"),
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "小米粥"),
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "豆浆"),
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "馄饨"),
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "牛奶"),
                            TurnTableNameData(toGroupName = builtinDataOneTitle, name = "八宝粥"),
                        )
                    }
                ),
                TurnTableNameWithGroup(
                    turnTableGroupData = TurnTableGroupData(groupName = builtinDataTwoTitle),
                    turnTableNameDataList = mutableListOf<TurnTableNameData>().apply {
                        mutableListInsert(
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "邯郸市区"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "青岛"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "西安"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "石家庄"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "浙江"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "上海"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "成都"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "重庆"),
                            TurnTableNameData(toGroupName = builtinDataTwoTitle, name = "宅在家里"),
                        )
                    }
                ),
                TurnTableNameWithGroup(
                    turnTableGroupData = TurnTableGroupData(groupName = builtinDataThreeTitle),
                    turnTableNameDataList = mutableListOf<TurnTableNameData>().apply {
                        mutableListInsert(
                            TurnTableNameData(
                                toGroupName = builtinDataThreeTitle, name = "发520红包"
                            ),
                            TurnTableNameData(
                                toGroupName = builtinDataThreeTitle, name = "跪洗衣板"
                            ),
                            TurnTableNameData(
                                toGroupName = builtinDataThreeTitle, name = "挨我一顿揍"
                            ),
                            TurnTableNameData(
                                toGroupName = builtinDataThreeTitle, name = "夸到我开心"
                            ),
                            TurnTableNameData(
                                toGroupName = builtinDataThreeTitle, name = "带我吃火锅"
                            ),
                            TurnTableNameData(
                                toGroupName = builtinDataThreeTitle, name = "写检讨书"
                            ),
                        )
                    }
                ),
                TurnTableNameWithGroup(
                    turnTableGroupData = TurnTableGroupData(groupName = builtinDataFourTitle),
                    turnTableNameDataList = mutableListOf<TurnTableNameData>().apply {
                        mutableListInsert(
                            TurnTableNameData(toGroupName = builtinDataFourTitle, name = "大冒险"),
                            TurnTableNameData(toGroupName = builtinDataFourTitle, name = "真心话"),
                            TurnTableNameData(
                                toGroupName = builtinDataFourTitle,
                                name = "再转一次"
                            ),
                            TurnTableNameData(toGroupName = builtinDataFourTitle, name = "大冒险"),
                            TurnTableNameData(toGroupName = builtinDataFourTitle, name = "真心话"),
                            TurnTableNameData(
                                toGroupName = builtinDataFourTitle,
                                name = "再转一次"
                            ),
                        )
                    }
                )
            )
        }
    }

    fun insertTurnTableEditData(data: TurnTableNameWithGroup, success: () -> Unit = {}) {
        launch({
            turnTableDataDao.insertTurnTableEditData(data.editDataToStringList())
        }, {
            success.invoke()
            "insertTurnTableEditData Success".loge()
        }, {
            "insertTurnTableEditData Error:${it.message},${it.stackTrace}".loge()
        })
    }

    fun selectGroup(groupName:String) {
        launch({
            turnTableDataDao.selectTurnTableGroup(groupName)
        },{
            "selectGroup Success".loge()
        },{
            "selectGroup Error:${it.message},${it.stackTrace}".loge()
        })
    }
}