package com.driver.reliantdispatch.presentation.sections

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.DELIVERY_INSPECTION
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.ResourcesGateway
import com.driver.reliantdispatch.domain.entities.DamageMark
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.min


class MarkingImageView: ImageView, View.OnTouchListener{

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val mPaint = Paint()
    private val mTextPaint = Paint()
    private var mRadius = resources.getDimension(R.dimen.damage_mark_radius)
    private var mRatio: Float = 1F
    private var mHorizMargin: Float = 0F
    private var mVertMargin: Float = 0F
    var mIntrinsicWidth: Float = 0F
    var mIntrinsicHeight: Float = 0F
    var path: String = ""   //TODO delete after debug

    @Inject
    lateinit var resourcesHelper: ResourcesGateway

    var damagesList: MutableList<DamageMark>? = null
        set(value){
            field = value
            this.invalidate()
        }

    var available = false
        set(value){
            setOnTouchListener(if (value) this else null)
            field = value
        }

    var miniature = false

    var damagesVisibility = true
        set(value){
            field = value
            this.invalidate()
        }

    var inspectionType: Int = -1
        set(value){
            mPaint.color = when(value){
                DELIVERY_INSPECTION -> resources.getColor(R.color.light_blue)
                else -> resources.getColor(R.color.orange_red)
            }
            mTextPaint.color = when(value){
                DELIVERY_INSPECTION -> resources.getColor(android.R.color.black)
                else -> resources.getColor(android.R.color.white)
            }
            field = value
        }


    init{
        setOnTouchListener(if (available) this else null)
        mPaint.color = resources.getColor(R.color.orange_red)
        //mPaint2.color = Color.CYAN
        mTextPaint.textSize = resources.getDimension(R.dimen.damage_mark_text_size)
        mTextPaint.color = resources.getColor(android.R.color.white)
        mTextPaint.style = Paint.Style.FILL

        App.component.inject(this)
    }

    fun removeLast(){
        damagesList?.let{
            if (it.size > 0) {
                it.removeAt(it.size-1)
                this.invalidate()
            }
        }
    }

    fun removeAll(){
        damagesList?.let{
            if (it.size > 0) {
                it.clear()
                this.invalidate()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (mIntrinsicWidth == 0F || mIntrinsicHeight == 0F) drawable?.let {
            mIntrinsicWidth = it.intrinsicWidth.toFloat()
            mIntrinsicHeight = it.intrinsicHeight.toFloat()
        }
        mRatio = min(measuredHeight.toFloat() / mIntrinsicHeight, measuredWidth.toFloat() / mIntrinsicWidth)
        mVertMargin = (measuredHeight - mIntrinsicHeight * mRatio) / 2
        mHorizMargin = (measuredWidth - mIntrinsicWidth * mRatio) / 2

        //Log.d(LOG_TAG, "measure $path - $mIntrinsicWidth x $mIntrinsicHeight")

        val resRadius = resources.getDimension(R.dimen.damage_mark_radius)
        mRadius = if (miniature) measuredHeight/10F else resRadius

        val resTextSize = resources.getDimension(R.dimen.damage_mark_text_size)
        mTextPaint.textSize = if (miniature) (mRadius/resRadius)*resTextSize else resTextSize
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //Log.d(LOG_TAG, "draw $path")
        if (canvas!=null && damagesList!=null && damagesVisibility)
            for (i in 0 until damagesList!!.size)
                drawDamage(canvas,
                    mHorizMargin + damagesList!![i].x * mRatio,
                    mVertMargin + damagesList!![i].y * mRatio,
                    damagesList!![i].damageKind
                )
    }

    private fun drawDamage(canvas: Canvas, x: Float, y: Float, kind: Int){
        //Log.d(LOG_TAG, "draw $x $y")

        canvas.drawCircle(x, y, mRadius, mPaint)
        val text = resourcesHelper.damageKindsShortArray[kind]
        val bounds = Rect()
        mTextPaint.getTextBounds(text, 0, text.length, bounds)

        /*val rect = RectF(bounds.left + x, bounds.top + y, bounds.right + x, bounds.bottom + y)
        canvas.drawRect(rect, mPaint2)*/

        val height = abs(bounds.top - bounds.bottom)
        val width = abs(bounds.right - bounds.left)
        canvas.drawText(text, x-1-width/2, y+height/2, mTextPaint)

        //canvas.drawText(text, x, y, mTextPaint)
    }

    private fun showSpinnerDialog(x: Int, y: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.dialog_title_damage)
        val kinds = resourcesHelper.damageKindsArray
        builder.setItems(kinds
        ) { dialog, pos ->
            damagesList?.add(DamageMark(0, 0, pos, x, y))
            this.invalidate()
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showRemoveDialog(foundDamage: DamageMark) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.dialog_title_warning)
        .setMessage(resources.getString(R.string.remove_damage_confirm, resourcesHelper.damageKindsShortArray[foundDamage.damageKind]))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()
                damagesList?.let{
                    it.remove(foundDamage)
                    this.invalidate()
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
        .create().show()
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                val x = (event.x - mHorizMargin) / mRatio
                val y = (event.y - mVertMargin) / mRatio
                if (x >= 0 && x < mIntrinsicWidth && y >= 0 && y < mIntrinsicHeight) {
                    val foundDamage = damagesList!!.find {
                        hypot(it.x - x, it.y - y) <= mRadius / mRatio
                    }
                    if (foundDamage != null) showRemoveDialog(foundDamage)
                    else showSpinnerDialog(x.toInt(), y.toInt())
                }
            }
        }
        return true
    }
}