package com.example.myapplication.ui.theme.transaction

data class CarbonCreditItem(
    val key: String? = null, // Firebase 데이터베이스에서 자동 생성된 고유 키
    val 판매자ID: String = "",
    val 판매물품개수: Int = 0,
    val 판매물품가격: Int = 0
)