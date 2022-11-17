package com.driver.reliantdispatch.data.glide

import android.graphics.BitmapFactory
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder

class OptionsSizeResourceTranscoder : ResourceTranscoder<BitmapFactory.Options, SizeGlide> {
    override fun transcode(resource: Resource<BitmapFactory.Options>, options: Options): Resource<SizeGlide> {
        val bmOptions = resource.get()
        val size = SizeGlide(bmOptions.outWidth, bmOptions.outHeight)
        return SimpleResource(size)
    }
}