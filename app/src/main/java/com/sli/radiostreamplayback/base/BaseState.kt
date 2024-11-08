package com.sli.radiostreamplayback.base

interface BaseState {
    val error: Reason?
    val progress: Boolean?
}