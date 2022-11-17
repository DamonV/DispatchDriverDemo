package com.driver.reliantdispatch.presentation

import android.content.Intent
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.databinding.ObservableField
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.DetailsUseCase
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.launch
import java.lang.Exception

class DetailsViewModel : ScopedViewModel() {
    val mItem = ObservableField<EbolJoined>()
    //var mScrollPosition = 0

    fun onOpenFile(){
        uiScope.launch {
            try {
                val uri = DetailsUseCase().openFile("DispatchSheet", "pdf")
                Log.d(LOG_TAG, "file ready: $uri")

                if (uri!=null) {
                    val newIntent = Intent(Intent.ACTION_VIEW)
                    newIntent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"))
                    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    viewModelEvent.value = PresentationEvent(
                        EventType.INTENT,
                        -1,
                        -1,
                        null,
                        newIntent
                    )
                } else {
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        context.getString(R.string.error_cant_download)
                    )
                }
            } catch (e: Exception){
                viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    e.message
                )
            }
        }
    }

}