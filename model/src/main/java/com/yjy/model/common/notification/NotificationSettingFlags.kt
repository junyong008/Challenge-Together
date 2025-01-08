package com.yjy.model.common.notification

/**
 * 비트 플래그를 사용하여 알림 설정을 관리
 *
 * 각 설정은 2의 거듭제곱(비트 시프트 연산)으로 표현:
 * ALL                     = 1          = 00000001
 * GOAL_ACHIEVED           = 2          = 00000010
 * WAITING_ROOM_UPDATE     = 4          = 00000100
 * ...
 *
 * 하나의 Int 값으로 여러 설정의 ON/OFF 상태를 저장 가능.
 * 예: 00000011 = 전체 알림과 목표 달성 알림이 켜진 상태
 *
 * 기본값:
 * 모두 꺼짐 = 0          = 00000000
 * 모두 켜짐 = 255 (0xFF) = 11111111
 *
 * 설정 확인: (settings & FLAG) != 0
 * 설정 켜기: settings | FLAG
 * 설정 끄기: settings & ~FLAG
 */
object NotificationSettingFlags {
    const val ALL = 1 shl 0
    const val GOAL_ACHIEVED = 1 shl 1
    const val WAITING_ROOM_PARTICIPANTS = 1 shl 2
    const val WAITING_ROOM_UPDATE = 1 shl 3
    const val CHALLENGE_ROOM_PROGRESS = 1 shl 4
    const val CHALLENGE_BOARD = 1 shl 5
    const val COMMUNITY_COMMENT = 1 shl 6
    const val COMMUNITY_REPLY = 1 shl 7

    fun isEnabled(settings: Int, flag: Int): Boolean {
        return (settings and flag) != 0
    }
}
