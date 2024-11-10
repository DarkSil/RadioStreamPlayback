package com.sli.radiostreamplayback.main.model

import com.sli.radiostreamplayback.base.BaseState
import com.sli.radiostreamplayback.base.Reason

data class MainState(
    override val error: Reason? = null,
    override val progress: Boolean = false,
    val radioList: RadioList? = null
) : BaseState
