package com.maunc.toolbox.pushbox.constant

/**
 * 方向键的大小配置
 */
const val PUSH_BOX_CONTROLLER_SIZE_MIN = 1
const val PUSH_BOX_CONTROLLER_SIZE_MIN_TWO = 2
const val PUSH_BOX_CONTROLLER_SIZE_MEDIUM = 3
const val PUSH_BOX_CONTROLLER_SIZE_MAX_TWO = 4
const val PUSH_BOX_CONTROLLER_SIZE_MAX = 5

enum class PushBoxMoveDirection {
    UP, DOWN, LEFT, RIGHT
}

/**
 * 0无场景  1墙   2目标   3路   4箱子   5目标区域   6人
 */
const val WALL: Int = 1//墙
const val GOAL = 2 //目标
const val ROAD = 3 //路
const val BOX = 4 //箱子
const val BOX_AT_GOAL = 5 //目标箱子(箱子和目标重合)
const val MAN = 6 //人

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

/**
 * 生成纯原始地图（仅保留墙、路、目标点）
 * 核心：把游戏地图中的 4(箱子)、5(箱子+目标点)、6(人) 都替换为 2(目标点)或3(路)
 */
fun obtainTargetPureOriginalMap(gradeIndex: Int): Array<IntArray> {
    val gameMap = obtainTargetMap(gradeIndex)
    return Array(gameMap.size) { i ->
        IntArray(gameMap[i].size) { j ->
            when (gameMap[i][j]) {
                WALL -> WALL          // 1=墙
                GOAL -> GOAL          // 2=目标点
                ROAD -> ROAD          // 3=路
                BOX -> ROAD           // 4=箱子 → 原始是路
                BOX_AT_GOAL -> GOAL   // 5=箱子+目标点 → 原始是目标点
                MAN -> ROAD           // 6=人 → 原始是路
                else -> ROAD          // 其他 → 路
            }
        }
    }
}