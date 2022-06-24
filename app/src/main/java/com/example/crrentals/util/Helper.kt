package com.example.crrentals.util

const val JPG_SUFFIX = ".jpg"
const val SHEET_STR_KEY = "sheet_str_key"

enum class BottomSheetAction {
    ADD,
    UPDATE
}

fun stringToInt(num: String) = if (num == "") null else num.toInt()