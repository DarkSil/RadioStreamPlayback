package com.sli.radiostreamplayback.main.model

import com.sli.radiostreamplayback.R

enum class SortType(val stringId: Int) {
    NAME(R.string.filter_name),
    RELIABILITY_ASC(R.string.filter_reliability_asc),
    RELIABILITY_DESC(R.string.filter_reliability_desc),
    POPULARITY_ASC(R.string.filter_popularity_asc),
    POPULARITY_DESC(R.string.filter_popularity_desc)
}