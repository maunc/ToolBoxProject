package com.maunc.toolbox.pushbox.constant

/*
     * 0 无场景
     * 1 墙
     * 2 目标
     * 3 路
     * 4 箱子
     * 5 目标区域
     * 6 人
     */
const val WALL: Int = 1//墙
const val GOAL = 2 //目标
const val ROAD = 3 //路
const val BOX = 4 //箱子
const val BOX_AT_GOAL = 5 //目标箱子
const val MAN = 6 //人


/**
 * 所有的关卡内容
 */
var allGradesMapData: ArrayList<ArrayList<ArrayList<Int>>> = arrayListOf(
    arrayListOf(
        arrayListOf(0, 0, 1, 1, 1, 0, 0, 0),
        arrayListOf(0, 0, 1, 2, 1, 0, 0, 0),
        arrayListOf(0, 0, 1, 3, 1, 1, 1, 1),
        arrayListOf(1, 1, 1, 4, 3, 4, 2, 1),
        arrayListOf(1, 2, 3, 4, 6, 1, 1, 1),
        arrayListOf(1, 1, 1, 1, 4, 1, 0, 0),
        arrayListOf(0, 0, 0, 1, 2, 1, 0, 0),
        arrayListOf(0, 0, 0, 1, 1, 1, 0, 0)
    ),
    arrayListOf(
        arrayListOf(1, 1, 1, 1, 1, 0, 0, 0, 0),
        arrayListOf(1, 3, 3, 6, 1, 0, 0, 0, 0),
        arrayListOf(1, 3, 4, 4, 1, 0, 1, 1, 1),
        arrayListOf(1, 3, 4, 3, 1, 0, 1, 2, 1),
        arrayListOf(1, 1, 1, 3, 1, 1, 1, 2, 1),
        arrayListOf(0, 1, 1, 3, 3, 3, 3, 2, 1),
        arrayListOf(0, 1, 3, 3, 3, 1, 3, 3, 1),
        arrayListOf(0, 1, 3, 3, 3, 1, 1, 1, 1),
        arrayListOf(0, 1, 1, 1, 1, 1, 0, 0, 0)
    ),
    arrayListOf(
        arrayListOf(0, 1, 1, 1, 1, 1, 1, 1, 0, 0),
        arrayListOf(0, 1, 3, 3, 3, 3, 3, 1, 1, 1),
        arrayListOf(1, 1, 4, 1, 1, 1, 3, 3, 3, 1),
        arrayListOf(1, 6, 3, 3, 4, 3, 3, 4, 3, 1),
        arrayListOf(1, 3, 2, 2, 1, 3, 4, 3, 1, 1),
        arrayListOf(1, 1, 2, 2, 1, 3, 3, 3, 1, 0),
        arrayListOf(0, 1, 1, 1, 1, 1, 1, 1, 1, 0)
    ),
    arrayListOf(
        arrayListOf(0, 1, 1, 1, 1, 0),
        arrayListOf(1, 1, 3, 3, 1, 0),
        arrayListOf(1, 3, 6, 4, 1, 0),
        arrayListOf(1, 1, 4, 3, 1, 1),
        arrayListOf(1, 1, 3, 4, 3, 1),
        arrayListOf(1, 2, 4, 3, 3, 1),
        arrayListOf(1, 2, 2, 3, 2, 1),
        arrayListOf(1, 1, 1, 1, 1, 1)
    ),
    arrayListOf(
        arrayListOf(0, 1, 1, 1, 1, 1, 1, 1),
        arrayListOf(0, 1, 3, 3, 3, 3, 3, 1),
        arrayListOf(0, 1, 3, 2, 4, 2, 3, 1),
        arrayListOf(1, 1, 3, 4, 6, 4, 3, 1),
        arrayListOf(1, 3, 3, 2, 4, 2, 3, 1),
        arrayListOf(1, 3, 3, 3, 3, 3, 3, 1),
        arrayListOf(1, 1, 1, 1, 1, 1, 1, 1),
    ),
    arrayListOf(
        arrayListOf(1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1),
        arrayListOf(1, 3, 3, 3, 3, 1, 1, 1, 3, 3, 3, 1),
        arrayListOf(1, 3, 4, 4, 3, 3, 3, 3, 3, 1, 6, 1),
        arrayListOf(1, 3, 4, 3, 1, 2, 2, 2, 3, 3, 3, 1),
        arrayListOf(1, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1, 1),
        arrayListOf(1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0),
    ),
    arrayListOf(
        arrayListOf(1, 1, 1, 1, 1, 1, 1),
        arrayListOf(1, 3, 3, 3, 3, 3, 1),
        arrayListOf(1, 3, 2, 4, 2, 3, 1),
        arrayListOf(1, 3, 4, 2, 4, 3, 1),
        arrayListOf(1, 3, 2, 4, 2, 3, 1),
        arrayListOf(1, 3, 4, 2, 4, 3, 1),
        arrayListOf(1, 3, 3, 6, 3, 3, 1),
        arrayListOf(1, 1, 1, 1, 1, 1, 1),
    ),
    arrayListOf(
        arrayListOf(0, 0, 1, 1, 1, 1, 1, 1),
        arrayListOf(0, 0, 1, 3, 2, 2, 6, 1),
        arrayListOf(0, 0, 1, 3, 4, 4, 3, 1),
        arrayListOf(0, 0, 1, 1, 3, 1, 1, 1),
        arrayListOf(0, 0, 0, 1, 3, 1, 0, 0),
        arrayListOf(0, 0, 0, 1, 3, 1, 0, 0),
        arrayListOf(1, 1, 1, 1, 3, 1, 0, 0),
        arrayListOf(1, 3, 3, 3, 3, 1, 1, 0),
        arrayListOf(1, 3, 1, 3, 3, 3, 1, 0),
        arrayListOf(1, 3, 3, 3, 1, 3, 1, 0),
        arrayListOf(1, 1, 1, 3, 3, 3, 1, 0),
        arrayListOf(0, 0, 1, 1, 1, 1, 1, 0),
    ),
)  //该9
/**
 * 0无场景  1墙  2目标  3路  4箱子  5目标区域  6人
 */

/**
 * 根据传入的关卡获取对应的地图
 */
fun obtainTargetMap(targetGradeIndex: Int): Array<IntArray> {
    val temp: ArrayList<ArrayList<Int>> =
        if (targetGradeIndex >= 0 && targetGradeIndex < allGradesMapData.size) {
            allGradesMapData[targetGradeIndex]
        } else {
            allGradesMapData[0]
        }
    val row = temp.size
    val column = temp[0].size
    val result = Array(row) {
        IntArray(column)
    }
    for (i in 0 until row) {
        for (j in 0 until column) {
            result[i][j] = temp[i][j]
        }
    }
    return result
}