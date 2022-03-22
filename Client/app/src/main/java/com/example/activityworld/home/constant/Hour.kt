package com.example.activityworld.home.constant

import com.example.activityworld.home.utilities.minutesToMilliseconds

enum class Hour (val hourInMinutes: Long) {
    ZERO(0),
    ONE(60),
    TWO(120),
    TREE(180),
    FOUR(240),
    FIVE(300),
    SIX(360),
    SEVEN(420),
    EIGHT(480),
    NINE(540),
    TEN(600),
    ELEVEN(660),
    TWELVE(720),
    THIRTEEN(780),
    FOURTEEN(840),
    FIFTEEN(900),
    SIXTEEN(960),
    SEVENTEEN(1020),
    EIGHTEEN(1080),
    NINETEEN(1140),
    TWENTY(1200),
    TWENTY_ONE(1260),
    TWENTY_TWO(1320),
    TWENTY_TREE(1380),
    TWENTY_FOUR(1440)
}

fun Hour.toMilliseconds(): Long {
    return minutesToMilliseconds(this.hourInMinutes)
}
