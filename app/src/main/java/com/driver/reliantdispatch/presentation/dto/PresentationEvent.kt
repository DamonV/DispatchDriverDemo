package com.driver.reliantdispatch.presentation.dto

import android.content.Intent
import android.os.Bundle

enum class EventType{
    NAVIGATE, NAVIGATE_BOTTOM, NAVIGATE_UP, SHOW_DIALOG, SHOW_TOAST, INTENT, NO_INTERNET,
    REPEAT_ACTION, SHOW_PROGRESS, DISMISS_PROGRESS
}

data class PresentationEvent(
    val type: EventType,
    val navDest: Int = -1,
    val dialogTitleResId: Int = -1,
    val dialogMsg: String? = null,
    val intent: Intent? = null,
    val args: Bundle? = null
    //val sourceClass: String? = null     //TODO delete after debugging
)