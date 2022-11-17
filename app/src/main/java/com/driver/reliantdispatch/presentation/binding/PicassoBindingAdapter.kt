package com.driver.reliantdispatch.presentation.binding

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.data.glide.SizeGlide
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.presentation.dto.GlideFragmentImage
import com.driver.reliantdispatch.presentation.sections.MarkingImageView
import java.util.*


object PicassoBindingAdapter {
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImage(view: ImageView, imageUrl: String?) {
        imageUrl?.let{
            //val picasso = Picasso.with(view.context)
            //picasso.setIndicatorsEnabled(true)
            //picasso.load(it).error(R.drawable.no_image).into(view)
            Log.d(LOG_TAG, it)

            Glide.with(view.context).load(it).placeholder(R.drawable.no_image).into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("image")
    fun setImageWithFragment(view: MarkingImageView, image: GlideFragmentImage?) {
        image?.run{
            //val picasso = Picasso.with(view.context)
            //picasso.setIndicatorsEnabled(true)
            //picasso.load(it).error(R.drawable.no_image).into(view)
            //Log.d(LOG_TAG, "frag - $imageUrl - $fragment")

            /*val picasso = Picasso.Builder(view.context).listener { picasso, uri, exception ->
                    Log.d(LOG_TAG, "err - $exception")
                }.build()
            picasso.load(imageUrl).error(R.drawable.no_image)/*.resize(0, 1080)*/.into(view)*/


            val time1 = Date().time
            Glide.with(fragment)
                .`as`(SizeGlide::class.java)
                .apply(
                    RequestOptions()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.DATA))
                .load(imageUrl)
                .into(MySimpleTarget(imageUrl, fragment, view, time1))

        }
    }

    class MySimpleTarget(
        val imageUrl: String?,
        val fragment: Fragment,
        val view: MarkingImageView,
        val time1: Long
    ) : SimpleTarget<SizeGlide>() {

        override fun onResourceReady(size: SizeGlide, glideAnimation: Transition<in SizeGlide>?) {
            view.mIntrinsicWidth = size.width.toFloat()
            view.mIntrinsicHeight = size.height.toFloat()
            view.path = imageUrl!!

            val ms = Date().time - time1
            //Log.d(LOG_TAG, "size $ms ms  $imageUrl = ${size}")

            //val time2 = Date().time
            Glide.with(fragment).load(imageUrl).error(R.drawable.no_image)
                .apply(
                    RequestOptions()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(view)
                /*.into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(size: Drawable, glideAnimation: Transition<in Drawable>?) {
                        val ms = Date().time - time2
                        Log.d(LOG_TAG, "load $ms ms  $imageUrl")
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                    }
                })*/

        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
        }
    }
}