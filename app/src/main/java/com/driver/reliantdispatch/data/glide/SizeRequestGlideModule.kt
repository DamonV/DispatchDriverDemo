package com.driver.reliantdispatch.data.glide

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.model.UriLoader
import com.bumptech.glide.module.AppGlideModule
import java.io.File
import java.io.InputStream

@GlideModule
class SizeRequestGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        //glide.registry.replace(Uri::class.java, InputStream::class.java, UriLoader.StreamFactory(context.contentResolver))
        registry.prepend(File::class.java, BitmapFactory.Options::class.java, BitmapSizeDecoder())
        registry.register(BitmapFactory.Options::class.java, SizeGlide::class.java, OptionsSizeResourceTranscoder())
    }
}